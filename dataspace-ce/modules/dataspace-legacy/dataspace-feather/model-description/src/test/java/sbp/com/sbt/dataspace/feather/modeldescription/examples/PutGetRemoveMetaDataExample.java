package sbp.com.sbt.dataspace.feather.modeldescription.examples;

import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;

/**
 * Bookmarking, reading and deleting metadata
 */
class PutGetRemoveMetaDataExample {

    /**
     * Execute
     *
     * @param modelDescription Model description
     */
    void run(ModelDescription modelDescription) {
// Initialize metadata
        ModelDescriptionMetaData.init(modelDescription);

// Get model version from metadata
        String version = modelDescription.getMetaDataManager().get(ModelDescriptionMetaData.class).version;

// Delete metadata
        ModelDescriptionMetaData.remove(modelDescription);
    }
}
