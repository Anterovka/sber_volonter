package sbp.com.sbt.dataspace.feather.modeldescription;

/**
 * Description of the link
 */
public interface ReferenceDescription extends PropertyDescriptionWithEntityDescription, PropertyDescriptionWithMandatory, PropertyDescriptionWithColumnName {

    /**
     * Get the name of the entity-reference property
     */
    String getEntityReferencePropertyName();

    /**
     * Get the name of the property collection referring to entities
     */
    String getEntityReferencesCollectionPropertyName();
}
