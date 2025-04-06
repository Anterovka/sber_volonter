package sbp.com.sbt.dataspace.feather.modeldescription;

import java.util.Map;

/**
 * Description of grouping
 */
public interface GroupDescription extends PropertyDescription {

    /**
     * Get the name of the grouping
     */
    // NotNull
    String getGroupName();

    /**
     * Get descriptions of primitives
     */
    // NotNull
    Map<String, PrimitiveDescription> getPrimitiveDescriptions();

    /**
     * Get primitive description
     *
     * @param propertyName Property name
     */
    // NotNull
    PrimitiveDescription getPrimitiveDescription(String propertyName);

    /**
     * Get link descriptions
     */
    // NotNull
    Map<String, ReferenceDescription> getReferenceDescriptions();

    /**
     * Get link description
     *
     * @param propertyName Property name
     */
    // NotNull
    ReferenceDescription getReferenceDescription(String propertyName);
}
