package sbp.com.sbt.dataspace.feather.modeldescription.examples;

import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;

import java.util.Set;

/**
 * Obtaining a list of entity types
 */
class GetEntityTypesExample {

    /**
     * Execute
     *
     * @param modelDescription Model description
     * @return List of entity types
     */
    Set<String> run(ModelDescription modelDescription) {
        return modelDescription.getEntityDescriptions().keySet();
    }
}
