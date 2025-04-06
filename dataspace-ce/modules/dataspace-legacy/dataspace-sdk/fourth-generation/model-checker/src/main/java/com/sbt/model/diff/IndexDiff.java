package com.sbt.model.diff;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.ParameterContext;
import com.sbt.mg.ElementState;
import com.sbt.mg.data.model.XmlIndex;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class IndexDiff implements DiffHandler {

    private static final Logger LOGGER = Logger.getLogger(IndexDiff.class.getName());

    private boolean disableCompatibilityCheck;

    public IndexDiff(boolean disableCompatibilityCheck) {
        this.disableCompatibilityCheck = disableCompatibilityCheck;
    }

    @Override
    public void handler(XmlModel newModel, XmlModel baseModel, ParameterContext parameterContext) {
        ModelParameters modelDiff = parameterContext.getModelParameters();

        LOGGER.info("\n\n  The comparison phase of indexes (beginning)");
        checkDiffIndex(newModel, baseModel, modelDiff);
        if (disableCompatibilityCheck) {
            // TODO figure out why and for what
            makeDeprecatedPKIndexForEmbeddable(baseModel);
        }
        LOGGER.info("\n\n  The comparison of indices phase (completion)");
    }

    private void checkDiffIndex(XmlModel newModel, XmlModel oldModel, ModelParameters modelDiff) {

        newModel.getClassesAsList().stream()
                .filter(xmlModelClass -> oldModel.containsClass(xmlModelClass.getName()))
                .forEach(newXmlModelClass -> {
                    XmlModelClass oldClass = oldModel.getClass(newXmlModelClass.getName());
                    List<XmlIndex> oldIndexis = new ArrayList<>(oldClass.getShouldDeletedIndices());

                    List<XmlIndex> indexIntersection = new ArrayList<>();

                    /* Sign of any change in index composition */
                    MutableBoolean indexListChanged = new MutableBoolean(false);
                    /* Unique index addition flag */
                    MutableBoolean uniqueIndexAdded = new MutableBoolean(false);
                    /* Sign of deleting unique index **/
                    MutableBoolean uniqueIndexRemoved = new MutableBoolean(false);


                    // We bypass the indices from model.xml to find out which indices need to be created
                    newXmlModelClass.getIndices().forEach(newIndex -> {
                        Optional<XmlIndex> any = oldIndexis.stream()
                                .filter(baseXmlIndex -> baseXmlIndex.equals(newIndex)).findAny();

                        if (!any.isPresent()) {
                            modelDiff.addDiffObject(ElementState.NEW, newIndex);
                            oldClass.addIndex(newIndex);
                            indexListChanged.setTrue();
                            if (newIndex.isUnique()) {
                                uniqueIndexAdded.setTrue();
                            }
                        } else {
                            // saving the overlapping index from oldModel,
                            // so that it would be easier to find the indexes for deletion.
                            indexIntersection.add(any.get());
                        }
                    });

                    // We bypass the indices from pdm to find out which indices need to be deleted
                    oldIndexis.forEach(oldIndex -> {
                        if (!indexIntersection.contains(oldIndex) && !oldIndex.isPrimaryKey()) {
                            modelDiff.addDiffObject(ElementState.REMOVED, oldIndex);
                            oldIndex.setDeprecated();
                            indexListChanged.setTrue();
                            if (oldIndex.isUnique()) {
                                uniqueIndexRemoved.setTrue();
                            }
                        }
                    });

                    // Check that unchanged indices have not had their field types modified on which they are based
                    indexIntersection.forEach(commonIndex -> {
                        boolean indexPropertyTypeChanged = commonIndex.getProperties().stream()
                                .map(indexProp -> indexProp.getProperty())
                                .anyMatch(prop -> prop.propertyChanged(XmlModelClassProperty.TYPE_TAG));
                        if (indexPropertyTypeChanged) {
                            modelDiff.addDiffObject(ElementState.UPDATED, commonIndex);
                            indexListChanged.setTrue();
                        }
                    });

                    if (indexListChanged.booleanValue()) {
                        LOGGER.warning(" -------------- A change in the indices of the class " + newXmlModelClass.getName() + " has been noticed." +
                        ". Requires conducting HT of all operations with data and HT rollout of changes");
                    }

                    if (uniqueIndexAdded.booleanValue()) {
                        LOGGER.warning(" -------------- A unique index has been noticed added to the class" + newXmlModelClass.getName() +
                            " . The text should be checked to ensure that there are no non-unique data combinations across all stands.");
                    }

                    if (uniqueIndexRemoved.booleanValue()) {
                        LOGGER.warning(" -------------- It has been noticed that the unique index of class" + newXmlModelClass.getName() + "has been deleted." +
                            "It is necessary to ensure that there is no dependency of the workflow logic on the uniqueness control by the combination of fields.");
                    }
                });
    }

    /**
     * Handles when converting class -> embeddable class.
     * Here we simply mark it as deprecated to subsequently remove from pdm
     * not added to the REMOVED collection, as there is no separate script for dropping the PK generated -
     * The table is cascadedly deleted, and in the rollback on dropTable, the restoration of the table with PK is written.
     * If this changes and you have to drop the PK separately, then here you will have to add some code to handle it.
     * modelDiff.addDiffObject(ElementState.REMOVED, xmlIndex)
     *
     * @param baseModel
     */
    private void makeDeprecatedPKIndexForEmbeddable(XmlModel baseModel) {
        baseModel.getClassesAsList()
                .stream()
                .filter(modelClass -> modelClass.isEmbeddable())
                .forEach(modelClass ->
                        modelClass.getIndices().stream()
                                .filter(XmlIndex::isPrimaryKey)
                                .findFirst()
                                .ifPresent(xmlIndex ->
                                        xmlIndex.setDeprecated(true)));
    }
}
