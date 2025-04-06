package sbp.com.sbt.dataspace.feather.modeldescription;

/**
 * Checking model description
 */
// SpringBeans
public interface ModelDescriptionCheck {

    /**
     * Get description
     */
    // NotNull
    String getDescription();

    /**
     * Check
     *
     * @param modelDescription Model description
     */
    void check(ModelDescription modelDescription);
}
