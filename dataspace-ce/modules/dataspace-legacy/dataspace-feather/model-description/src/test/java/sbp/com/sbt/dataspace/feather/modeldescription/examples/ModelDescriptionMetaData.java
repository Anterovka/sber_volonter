package sbp.com.sbt.dataspace.feather.modeldescription.examples;

import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;

/**
 * Model description metadata
 */
class ModelDescriptionMetaData {

    static final Object WRITE_KEY = new Object();

    String version;

    /**
     * Initialize metadata
     *
     * @param modelDescription Model description
     */
    static void init(ModelDescription modelDescription) {
        ModelDescriptionMetaData modelDescriptionMetaData = new ModelDescriptionMetaData();
        modelDescriptionMetaData.version = System.getProperty("version");
        modelDescription.getMetaDataManager().put(ModelDescriptionMetaData.class, WRITE_KEY, modelDescriptionMetaData);
    }

    /**
     * Delete metadata
     *
     * @param modelDescription Model description
     */
    static void remove(ModelDescription modelDescription) {
        modelDescription.getMetaDataManager().remove(ModelDescriptionMetaData.class, WRITE_KEY);
    }
}
