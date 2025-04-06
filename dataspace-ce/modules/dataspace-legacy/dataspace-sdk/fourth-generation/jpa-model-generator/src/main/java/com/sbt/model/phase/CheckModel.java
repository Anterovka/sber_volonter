package com.sbt.model.phase;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.ParameterContext;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.dataspace.pdm.StreamModel;
import com.sbt.dictionary.DictionaryGenerator;
import com.sbt.mg.Helper;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.XmlImport;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.exception.checkmodel.ModelFileNotSetException;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.model.checker.Checker;
import org.apache.commons.io.FileUtils;
import org.barfuin.texttree.api.DefaultNode;
import org.barfuin.texttree.api.TextTree;
import org.barfuin.texttree.api.TreeOptions;
import org.barfuin.texttree.api.style.TreeStyle;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.sbt.mg.Helper.isXmlModelFile;
import static com.sbt.mg.ModelHelper.XML_MAPPER;
import static com.sbt.mg.jpa.JpaConstants.DEFAULT_DICTIONARY_NAME_DIR;
import static com.sbt.mg.jpa.JpaConstants.ENTITY_CLASS_NAME;

public class CheckModel implements Phase<ModelParameters, PluginParameters> {
    private static final Logger LOGGER = Logger.getLogger(CheckModel.class.getName());

    public CheckModel() {
    }

    @Override
    public ModelParameters execute(PluginParameters pluginParameters) {
        LOGGER.info("Launching the model verification phase");
        ParameterContext parameterContext = new ParameterContext(
            pluginParameters,
                new ModelParameters()
                        .setModelDirectory(pluginParameters.getModel())
                        .setTargetModelPath(pluginParameters.getTargetFile()));

        StreamModel streamModel = convertToStreamModel(parameterContext, false);
        if(pluginParameters.getModelVersion() != null) {
            parameterContext.getModelParameters().setVersion(pluginParameters.getModelVersion());
        }

        if (pluginParameters.isDisableCompatibilityCheck()) {
            LOGGER.warning("\n\n************************************************************************************************************************\n" +
                    "* Attention! Backward compatibility check is disabled! (build launched with disableCompatibilityCheck flag enabled) *\n" +
                    "************************************************************************************************************************\n");
            ModelParameters modelParameters = execute(parameterContext, streamModel, true);
            printCompatibilityViolationMessage(modelParameters);
            return modelParameters;
        } else {
            ModelParameters modelParameters = execute(parameterContext, streamModel, false);
            /**
             * for intermediate releases, model generation and checker invocation happens twice:
             * - first time to compare the new model with the release one and not to miss backward-incompatible changes
             * Relative to the release model
             The original text does not contain any Russian words or phrases to be translated into English. Therefore, no replacements are needed.
             * Relatively to the last intermediate
             */
            if (pluginParameters.isIntermediaryBuild()) {
                LOGGER.info("Intermediate model verification phase launch");

                parameterContext = new ParameterContext(
                        pluginParameters,
                        new ModelParameters()
                                .setModelDirectory(pluginParameters.getModel())
                                .setTargetModelPath(pluginParameters.getTargetFile()));

                streamModel = convertToStreamModel(parameterContext, true);
                if (pluginParameters.getModelVersion() != null) {
                    parameterContext.getModelParameters().setVersion(pluginParameters.getModelVersion());
                }
                LOGGER.info("Disabling backward compatibility check");
                modelParameters = execute(parameterContext, streamModel, true);
            }
            return modelParameters;
        }
    }

    private void printCompatibilityViolationMessage(ModelParameters modelParameters) {
        List<String> backwardCompatibilityViolationMessages = modelParameters.getBackwardCompatibilityViolationMessages();
        if (!backwardCompatibilityViolationMessages.isEmpty()) {
            boolean oneMessage = backwardCompatibilityViolationMessages.size() == 1;
            String messageHeader = String.format("************************************************************************************************************************\n" +
                    "Attention! A registered change has been made that violates backward compatibility:",
                oneMessage ? "o" : "y",
                oneMessage ? "e" : "ya",
                oneMessage ? "ее" : "ее");
            String messages = String.join("\n", backwardCompatibilityViolationMessages);
            String messageBasement = "Such changes usually lead to the need for subsequent data migration to the database, as well as to the need to reinitialize the StandIn replicas and KAP.\n" +
                    "************************************************************************************************************************\n";
            String fullMessage = String.join("\n", messageHeader, messages, messageBasement);
            LOGGER.log(Level.WARNING, () -> "\n\n" + fullMessage);
        }
    }

    private StreamModel convertToStreamModel(ParameterContext parameterContext, boolean isIntermediateBuild) {
        PluginParameters pluginParameters = parameterContext.getPluginParameters();
        // We get the path to model.xml
        File modelFile = Helper.getFile(pluginParameters.getModel(), pluginParameters.getModelName());
        if (!modelFile.exists()) {
            throw new ModelFileNotSetException(pluginParameters.getModel().getAbsolutePath(), pluginParameters.getModelName());
        }

        // Push the stream with model.xml to the repository
        StreamModel streamModel = StreamModel.forModelStreamOnly()
                .setRootModel(Helper.wrap(() -> new FileInputStream(modelFile)));

        File lastDirModel;
        File userLocalModelDir = Helper.getFile(pluginParameters.getModel(), JpaConstants.userLocalModelDir());
        if (!pluginParameters.isLocalDeploy() || !userLocalModelDir.exists() || !userLocalModelDir.isDirectory()) {
            lastDirModel = parameterContext.getModelParameters().getTargetModelPath();
        } else {
            File localLastDirModel = Helper.createDirectory(pluginParameters.getModel(), JpaConstants.userLocalModelDir());
            File localLastModelFile = Helper.getFile(localLastDirModel, JpaConstants.PDM_MODEL_FILENAME);

            if (pluginParameters.isLocalDeploy() && localLastModelFile.exists()) {
                lastDirModel = localLastDirModel;
            } else {
                lastDirModel = parameterContext.getModelParameters().getTargetModelPath();
            }
        }

        // We try to get pdm.xml - that is, the previous version of the model
        File lastModelFile = Helper.getFile(lastDirModel, JpaConstants.PDM_MODEL_FILENAME);
        if (!isIntermediateBuild) {
            if (lastModelFile.exists()) {
                streamModel.setPdm(Helper.wrap(() -> new FileInputStream(lastModelFile)));
                streamModel.setImmutablePdm(Helper.wrap(() -> new FileInputStream(lastModelFile)));
            }

        } else {
        // in the past model version, we use an intermediate pdm - pdm-build.xml
            File intermediaryModelFile = Helper.getFile(lastDirModel, JpaConstants.PDM_BUILD_MODEL_FILENAME);
            if (intermediaryModelFile.exists()) {
                streamModel.setPdm(Helper.wrap(() -> new FileInputStream(intermediaryModelFile)));
                streamModel.setImmutablePdm(Helper.wrap(() -> new FileInputStream(intermediaryModelFile)));
            } else {
                if (lastModelFile.exists()) {
                    streamModel.setPdm(Helper.wrap(() -> new FileInputStream(lastModelFile)));
                    streamModel.setImmutablePdm(Helper.wrap(() -> new FileInputStream(lastModelFile)));
                }
            }
        }

        // changelog
        final File changelogFile = Helper.getFile(lastDirModel, JpaConstants.CHANGELOG_FILENAME);
        if (changelogFile.exists()) {
            streamModel.setChangelog(Helper.getText(Helper.wrap(() -> new FileInputStream(changelogFile))));
        }
        parameterContext.getModelParameters().setPathToPreviousChangelog(changelogFile);

        addModelViaImportsExt(streamModel, modelFile, pluginParameters, parameterContext.getModelParameters(), lastDirModel);

        return streamModel;
    }

    private void addModelViaImportsExt(StreamModel streamModel,
                                       File modelFile,
                                       PluginParameters pluginParameters,
                                       ModelParameters modelParameters,
                                       File lastDirModel) {
        File dbDirectory = pluginParameters.getModel();
        boolean optimizeChangelog = pluginParameters.isOptimizeChangelog();
        String typeImportName = "IMPORT";
        String nameDirImport = "import";

        XmlModel xmlModel = Helper.wrap(() -> XML_MAPPER.readValue(modelFile, XmlModel.class));
        //checking the root tag name is performed after loading the model for
        //interception of the parser error by the previous method
        ModelHelper.checkRootTagName(modelFile, XmlModel.MODEL_TAG);

        Optional<XmlImport> optionalImport = xmlModel.getImports().stream()
                .filter(xmlImport -> typeImportName.equals(xmlImport.getType()))
                .findFirst();

        File dir;

        if (optionalImport.isPresent()) {
            XmlImport xmlImport = optionalImport.get();
            String filePath = xmlImport.getFile();
            dir = Helper.getFile(dbDirectory, filePath != null ? filePath : nameDirImport);
        } else {
            dir = Helper.getFile(dbDirectory, nameDirImport);
        }

        Set<File> dictionaryFiles = new HashSet<>();
        if (optimizeChangelog) {
            if (lastDirModel.exists()) {
                File fullDictDir = new File(lastDirModel, DEFAULT_DICTIONARY_NAME_DIR);
                fillDictionaryFiles(dictionaryFiles, fullDictDir);
            }
        } else {
            fillFromModel(dictionaryFiles, dbDirectory, xmlModel);
        }

        if (!dir.exists()) {
            LOGGER.info("Directory " + dir.getPath() + " with imported classes not found");
        } else {
            // Adding child models and filling files where to get reference data from.
            FileUtils.iterateFiles(dir, new String[]{"xml"}, true)
                    .forEachRemaining(file -> {
                        if (isXmlModelFile(file)) {
                            //if the user makes a mistake in the name of the model tag, the imported model will not be loaded!
                            if (!optimizeChangelog) {
                                Helper.wrap(() -> {
                                    XmlModel childModel = XML_MAPPER.readValue(new FileInputStream(file), XmlModel.class);
                                    fillFromModel(dictionaryFiles, file.getParentFile(), childModel);
                                });
                            }

                            streamModel.getModel().add(Helper.wrap(() -> new FileInputStream(file)));
                        }

                    });
        }

        modelParameters.getDictionaryFiles().addAll(dictionaryFiles);
    }

    private void fillFromModel(Set<File> dictionaryFiles, File dbDirectory, XmlModel model) {
        String modelDir = DictionaryGenerator.defineImportFolder(model);
        File fullDictDir = new File(dbDirectory, modelDir);
        fillDictionaryFiles(dictionaryFiles, fullDictDir);
    }

    private void fillDictionaryFiles(Set<File> dictionaryFiles, File fullDictDir) {
        if (fullDictDir.exists()) {
            FileUtils.iterateFiles(fullDictDir, new String[]{"json"}, true)
                    .forEachRemaining(dictionaryFiles::add);
        }
    }

    public ModelParameters execute(StreamModel streamModel) {
        return execute(ParameterContext.emptyContext(), streamModel, false);
    }

    private ModelParameters execute(ParameterContext parameterContext, StreamModel streamModel, boolean disableCompatibilityCheck) {

        new Checker(streamModel, parameterContext, disableCompatibilityCheck).check();

        printModelClassAsTree(parameterContext.getModelParameters().getModel());

        LOGGER.info("Model verification phase completion");
        return parameterContext.getModelParameters();
    }

    private void printModelClassAsTree(XmlModel model) {
        XmlModelClass baseEntity = model.getClass(ENTITY_CLASS_NAME);
        DefaultNode rootNode = new DefaultNode(baseEntity.getName());

        addSubTree(baseEntity, rootNode);

        TreeOptions options = new TreeOptions();
        options.setStyle(new TreeStyle("+- ", "|  ", "\\- "));
        options.setEnableDefaultColoring(true);

        String rendered = TextTree.newInstance(options).render(rootNode);

        LOGGER.info(model.toString() + '\n' + rendered);
    }

    private void addSubTree(XmlModelClass modelClass, DefaultNode rootNode) {
        rootNode.setText(rootNode.getText() + " <" + modelClass.getClassAccess() + ">");

        for (XmlModelClass modelClass1 : modelClass.getModel().getClassesAsList()) {
            if (modelClass.getName().equals(modelClass1.getExtendedClassName())) {
                DefaultNode childNode = new DefaultNode(modelClass1.getName());

                addSubTree(modelClass1, childNode);

                rootNode.addChild(childNode);
            }
        }
    }
}
