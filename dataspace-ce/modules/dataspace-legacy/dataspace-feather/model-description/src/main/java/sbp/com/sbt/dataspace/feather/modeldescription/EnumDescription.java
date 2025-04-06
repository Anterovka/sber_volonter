package sbp.com.sbt.dataspace.feather.modeldescription;

import java.util.Set;

/**
 * Enumeration Description
 */
public interface EnumDescription {

    /**
     * Get model description
     */
    // NotNull
    ModelDescription getModelDescription();

    /**
     * Get the name
     */
    // NotNull
    String getName();

    /**
     * Get values
     */
    // NotNull
    Set<String> getValues();
}
