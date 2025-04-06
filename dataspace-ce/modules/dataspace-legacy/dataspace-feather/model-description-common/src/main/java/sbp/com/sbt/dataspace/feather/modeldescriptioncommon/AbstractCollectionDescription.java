package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.CollectionDescription;

/**
 * Abstract description of collection
 */
class AbstractCollectionDescription extends AbstractPropertyDescriptionWithColumnName implements CollectionDescription {

    String tableName;
    String ownerColumnName;

    @Override
    void process(PropertyDescriptionSettings propertyDescriptionSettings) {
        super.process(propertyDescriptionSettings);
        AbstractCollectionDescriptionSettings<?> collectionDescriptionSettings = (AbstractCollectionDescriptionSettings<?>) propertyDescriptionSettings;
        tableName = collectionDescriptionSettings.tableName;
        ownerColumnName = collectionDescriptionSettings.ownerColumnName;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public String getOwnerColumnName() {
        return ownerColumnName;
    }
}
