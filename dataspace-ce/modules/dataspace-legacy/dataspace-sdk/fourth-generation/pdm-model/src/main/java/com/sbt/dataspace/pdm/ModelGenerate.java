package com.sbt.dataspace.pdm;

import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import org.apache.commons.lang3.mutable.MutableLong;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Interface that is taken into account during project compilation
 */
public interface ModelGenerate {

    /**
     * The method that sets the execution order of the generator during model initialization
     */
    default int getPriority() {
        return 50;
    }

    /**
     * The pre-configuration method of the project. It is always executed, regardless of the presence in the imports in the model.
     */
    default void preInit(XmlModel model, PluginParameters pluginParameters) {
    }

    /**
     * Import name, which when present in the model triggers this generator
     *
     * @return Project name
     */
    String getProjectName();

    // Add helper classes to the model. This is done before any initial checks.
    default void addServiceClassesToModel(XmlModel model, PluginParameters pluginParameters) {
    }

    /**
     * The initialization method of the functionality model. It will be executed if getProjectName() is in the imports of the model.
     *
     * @param file file specified in the import tag in the model
     */
    void initModel(XmlModel model, File file, ModelParameters modelParameters);

    default void initModel(XmlModel model, StreamModel streamModel, ModelParameters modelParameters) {
        initModel(model, (File) null, modelParameters);
    }

    /**
     * Method of additional initialization of functionalities that appear
     * later due to the priority of creation
     * This method is executed after the methods of initModel of all other generators are completed.
     *
     * @param model           model
     * @param file       file with the model
     * @param modelParameters model parameters
     */
    default void postInitModel(XmlModel model, File file, ModelParameters modelParameters) {
    }

    default void postInitModel(XmlModel model, ModelParameters modelParameters) {
        postInitModel(model, (File) null, modelParameters);
    }

    /**
     * Calculation of the difference between versions
     *
     * @param modelParameters current parameters of model change
     * @param pdmModel   Previous model
     */
    default void checkDiffs(ModelParameters modelParameters, PdmModel pdmModel) {
    }

    /**
     * Add additional interfaces to model classes
     *
     * @param modelClass class of processing
     * @return list of interfaces
     */
    default List<String> addInterfacesToJpaModel(XmlModelClass modelClass) {
        return Collections.emptyList();
    }

    /**
     * Add additional property to class
     *
     * @param modelClass class of processing
     * @return fully described method in the class Jpa
     */
    default String addProperty(XmlModelClass modelClass) {
        return "";
    }

    /**
     * Add additional method interface by model class
     *
     * @param modelClass class of processing
     * @return fully described method in the Jpa class
     */
    default String addMethod(XmlModelClass modelClass) {
        return "";
    }

    /**
     * Add imports to the class header
     *
     * @param modelClass class of processing
     * @return fully described import
     */
    default List<String> addImports(XmlModelClass modelClass) {
        return Collections.emptyList();
    }

    /**
     * Add additional annotations above the class
     *
     * @param modelClass class of processing
     * @return fully described import
     */
    default List<String> addClassAnnotations(XmlModelClass modelClass) {
        return Collections.emptyList();
    }

    /**
     * Add additional annotations above properties
     *
     * @param property класс обработки
     * @return fully described import
     */
    default List<String> addPropertyAnnotations(XmlModelClassProperty property) {
        return Collections.emptyList();
    }

    /**
     * Create model class
     *
     * @param modelDirectory path to the user's model
     * @param packageName        package name
     */
    default void createJavaClass(File modelDirectory, String packageName) {

    }

    /**
     * Adding data to the database
     *
     * @param changelogId     id генерации
     * @param model           basic model
     * @param modelParameters parameters of the model (changelog, pdm)
     * @return changelog
     */
    default String addDataToDB(MutableLong changelogId, XmlModel model, ModelParameters modelParameters) {
        return null;
    }

    /**
     * Save structure for server
     *
     * @param classCollector  model of pdm file
     * @param targetFile target directory
     */
    default void saveModel(PdmModel classCollector, TargetFileHolder targetFile) {
    }

    /**
     * Method to fix problems from previous changelog generations
     *
     * @param changelogText changelog text that will be processed
     * @return corrected text changelog
     */
    default String modificateChangelog(String changelogText) {
        return changelogText;
    }
}
