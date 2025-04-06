package com.sbt.model.checker;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.ParameterContext;
import com.sbt.dataspace.pdm.PdmModel;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.dataspace.pdm.StreamModel;
import com.sbt.dataspace.pdm.xml.XmlRootModel;
import com.sbt.dataspace.pdm.xml.XmlSourceModels;
import com.sbt.mg.Helper;
import com.sbt.mg.NameHelper;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.model.exception.ModelSHA256HashIsInvalidException;
import com.sbt.model.exception.NotAllModelNamesFilledException;
import com.sbt.model.exception.optimizechangelog.OldModelXmlNotFoundInPdmException;
import com.sbt.model.exception.optimizechangelog.PdmNotFoundException;
import com.sbt.parameters.enums.Changeable;
import sbp.com.sbt.semver.Semver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.sbt.mg.Helper.decompress;
import static com.sbt.mg.ModelHelper.XML_MAPPER;
import static com.sbt.mg.jpa.JpaConstants.JPA_DISCRIMINATOR_NAME;

public class CheckerUtils {

    private static final String TYPE_COLUMN_NAME = "TYPE";

    private CheckerUtils() {
        /* no-ops */
    }

    static XmlModel obtainModel(StreamModel streamModel, ParameterContext parameterContext, PdmModel pdmModel) {
        PluginParameters pluginParameters = parameterContext.getPluginParameters();

        XmlModel rootModel = Helper.wrap(() -> XML_MAPPER.readValue(streamModel.getRootModel(), XmlModel.class));

        List<XmlModel> importModels = streamModel.getModel().stream()
                .map(inputStream -> Helper.wrap(() -> XML_MAPPER.readValue(inputStream, XmlModel.class)))
                .collect(Collectors.toList());

        if (pluginParameters.isOptimizeChangelog()) {
            if (Objects.nonNull(pdmModel)) {
                XmlSourceModels sourceModels = pdmModel.getSourceModels();
                if (Objects.nonNull(sourceModels)
                        && Objects.nonNull(sourceModels.getPreviousRootModel())) {
                    XmlRootModel previousRootModel = sourceModels.getPreviousRootModel();
                    String strRootModel = Helper.wrap(() -> decompress(previousRootModel.getData()));
                    String sha256 = previousRootModel.getSha256();
                    if (!sha256.equals(Helper.sha256(strRootModel))) {
                        throw new ModelSHA256HashIsInvalidException(previousRootModel.getModelName());
                    }
                    rootModel = Helper.wrap(() -> XML_MAPPER.readValue(strRootModel, XmlModel.class));
                    importModels.clear();
                    sourceModels.getPreviousImportModels().forEach(xmlImportModel -> {
                        String strImportModel = Helper.wrap(() -> decompress(xmlImportModel.getData()));
                        String importSHA256 = xmlImportModel.getSha256();
                        if (!importSHA256.equals(Helper.sha256(strImportModel))) {
                            throw new ModelSHA256HashIsInvalidException(xmlImportModel.getModelName());
                        }
                        XmlModel importModel = Helper.wrap(() -> XML_MAPPER.readValue(strImportModel, XmlModel.class));
                        importModels.add(importModel);
                    });
                } else {
                    throw new OldModelXmlNotFoundInPdmException();
                }
            } else {
                throw new PdmNotFoundException();
            }
        }

        importModels.forEach(rootModel::importXmlModel);

        final List<XmlModel> allModels = new ArrayList<>();
        allModels.add(rootModel);
        allModels.addAll(importModels);

        checkModelStreamsHaveOnlyOneMainModel(allModels);
        return rootModel;
    }

    static void checkModelStreamsHaveOnlyOneMainModel(List<XmlModel> models) {
        long modelsWithNamesCount = models.stream()
                .filter(model -> model.getModelName() != null)
                .count();

        if (modelsWithNamesCount != models.size()) {
            throw new NotAllModelNamesFilledException();
        }
    }

    /**
     * Checking readiness of model element for final deletion (and script generation for deleting from DB)
     *
     * @param newModelVersion             - current (new) version of the model
     * @param deletedInVersion            - version of the model when the element was removed from pdm.xml
     * @param dropDeletedItemsImmediately - flag, depending on which we can delete an element in the same version of the model when it is deleted from pdm or a release above
     * @return
     */
    public static boolean isCorrectVersionToDelete(String newModelVersion, String deletedInVersion, boolean dropDeletedItemsImmediately) {
        if (dropDeletedItemsImmediately) {
            return Semver.of(newModelVersion).compareTo(Semver.of(deletedInVersion)) == 0;
        }
        return Semver.of(newModelVersion).compareTo(Semver.of(deletedInVersion)) > 0;
    }

    public static <T> void addValueToListMap(Map<String, List<T>> map, String key, T value) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList());
        }
        map.get(key).add(value);
    }

    public static <T> Map<String, List<T>> cloneListMap(Map<String, List<T>> map) {
        Map<String, List<T>> result = new HashMap<>(map);
        result.keySet()
                .forEach(key ->
                    result.put(key, new ArrayList<>(result.get(key)))
                );
        return result;
    }

    public static XmlModelClassProperty discriminatorField(String defaultValue, boolean useRenamedFields) {
        return XmlModelClassProperty.Builder.create()
                .setName(JPA_DISCRIMINATOR_NAME)
                .setType("String")
                .setLabel("Class Discriminator")
                .setMandatory(Boolean.TRUE)
                .setIndex(Boolean.TRUE)
                .setColumnName(
                        useRenamedFields ?
                                NameHelper.getName(
                                        TYPE_COLUMN_NAME,
                                        JpaConstants.DEFAULT_MAX_DB_OBJECT_NAME_LENGTH,
                                        true
                                ) :
                                TYPE_COLUMN_NAME
                )
                .setDescription("Discriminator")
                .setChangeable(Changeable.READ_ONLY)
                .setDefaultValue(defaultValue)
                .build();
    }
}
