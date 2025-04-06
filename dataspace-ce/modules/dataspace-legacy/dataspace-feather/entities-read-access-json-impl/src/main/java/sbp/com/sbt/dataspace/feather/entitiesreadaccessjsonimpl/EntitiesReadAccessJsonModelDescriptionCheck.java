package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.modeldescription.CheckEntityDescriptionException;
import sbp.com.sbt.dataspace.feather.modeldescription.CheckPropertyDescriptionException;
import sbp.com.sbt.dataspace.feather.modeldescription.CollectionDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.InheritanceStrategy;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PropertyDescriptionWithColumnName;
import sbp.com.sbt.dataspace.feather.modeldescription.TableType;
import sbp.com.sbt.dataspace.feather.stringexpressions.StringExpressionsModelDescriptionCheck;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.forEach;

/**
 * Checking model description for access to entities for reading through JSON
 */
class EntitiesReadAccessJsonModelDescriptionCheck extends StringExpressionsModelDescriptionCheck {

static final String INHERITANCE_STRATEGY_SETTING = "Inheritance strategy setting";
static final String TABLE_NAME_SETTING = "Table name setting";
static final String ID_COLUMN_NAME_SETTING = "The name of the column with an id";
static final String TYPE_COLUMN_NAME_SETTING = "Name of the column with type";
static final String AGGREGATE_COLUMN_NAME_SETTING = "Aggregate column name setting";
static final String SYSTEM_LOCKS_TABLE_NAME_SETTING = "Name of the system locks table";
static final String SYSTEM_LOCKS_AGGREGATE_COLUMN_NAME_SETTING = "Name of the column in the system locks table with an aggregate";
static final String SYSTEM_LOCKS_VERSION_COLUMN_NAME_SETTING = "The name of the system locks table column with version";
static final String COLUMN_NAME_SETTING = "Column name";
static final String OWNER_COLUMN_NAME_SETTING = "Name of the column with the owner";

    /**
* Check property description
     *
* @param propertyDescription Property description
     */
    void checkPropertyDescription(PropertyDescriptionWithColumnName propertyDescription) {
        if (propertyDescription.getColumnName() == null) {
            throw new RequiredSettingNotSetException(COLUMN_NAME_SETTING);
        }
    }

    /**
     * Check collection description
     *
     * @param collectionDescription Description of the collection
     */
    void checkCollectionDescription(CollectionDescription collectionDescription) {
        checkPropertyDescription(collectionDescription);
        if (collectionDescription.getTableName() == null) {
            throw new RequiredSettingNotSetException(TABLE_NAME_SETTING);
        }
        if (collectionDescription.getOwnerColumnName() == null) {
            throw new RequiredSettingNotSetException(OWNER_COLUMN_NAME_SETTING);
        }
    }

    /**
     * Check entity description
     */
    void checkEntityDescription(EntityDescription entityDescription) {
        if (entityDescription.getParentEntityDescription() == null) {
            if (entityDescription.getInheritanceStrategy() == null) {
                throw new RequiredSettingNotSetException(INHERITANCE_STRATEGY_SETTING);
            }
            if (entityDescription.getTableType() == TableType.SIMPLE) {
                if (entityDescription.getTableName() == null) {
                    throw new RequiredSettingNotSetException(TABLE_NAME_SETTING);
                }
            } else {
                if (entityDescription.getTableName() != null) {
                    throw new ExtraSettingFoundException(TABLE_NAME_SETTING);
                }
            }
            if (entityDescription.getTableType() == TableType.SIMPLE && entityDescription.getIdColumnName() == null) {
                throw new RequiredSettingNotSetException(ID_COLUMN_NAME_SETTING);
            }
            if (!entityDescription.isFinal() && entityDescription.getTypeColumnName() == null) {
                throw new RequiredSettingNotSetException(TYPE_COLUMN_NAME_SETTING);
            } else if (entityDescription.isFinal() && entityDescription.getTypeColumnName() != null) {
                throw new ExtraSettingFoundException(TYPE_COLUMN_NAME_SETTING);
            }
            if (entityDescription.getAggregateEntityDescription() != null && entityDescription.getAggregateColumnName() == null) {
                throw new RequiredSettingNotSetException(AGGREGATE_COLUMN_NAME_SETTING);
            } else if (entityDescription.getAggregateEntityDescription() == null && entityDescription.getAggregateColumnName() != null) {
                throw new ExtraSettingFoundException(AGGREGATE_COLUMN_NAME_SETTING);
            }
            if (entityDescription.isAggregate()) {
                if (entityDescription.getSystemLocksTableName() == null) {
                    throw new RequiredSettingNotSetException(SYSTEM_LOCKS_TABLE_NAME_SETTING);
                }
                if (entityDescription.getSystemLocksAggregateColumnName() == null) {
                    throw new RequiredSettingNotSetException(SYSTEM_LOCKS_AGGREGATE_COLUMN_NAME_SETTING);
                }
                if (entityDescription.getSystemLocksVersionColumnName() == null) {
                    throw new RequiredSettingNotSetException(SYSTEM_LOCKS_VERSION_COLUMN_NAME_SETTING);
                }
            } else {
                if (entityDescription.getSystemLocksTableName() != null) {
                    throw new ExtraSettingFoundException(SYSTEM_LOCKS_TABLE_NAME_SETTING);
                }
                if (entityDescription.getSystemLocksAggregateColumnName() != null) {
                    throw new ExtraSettingFoundException(SYSTEM_LOCKS_AGGREGATE_COLUMN_NAME_SETTING);
                }
                if (entityDescription.getSystemLocksVersionColumnName() != null) {
                    throw new ExtraSettingFoundException(SYSTEM_LOCKS_VERSION_COLUMN_NAME_SETTING);
                }
            }
        } else {
            if (entityDescription.getInheritanceStrategy() != null) {
                throw new ExtraSettingFoundException(INHERITANCE_STRATEGY_SETTING);
            }
            if (entityDescription.getRootEntityDescription().getInheritanceStrategy() == InheritanceStrategy.SINGLE_TABLE) {
                if (entityDescription.getTableName() != null) {
                    throw new ExtraSettingFoundException(TABLE_NAME_SETTING);
                }
                if (entityDescription.getIdColumnName() != null) {
                    throw new ExtraSettingFoundException(ID_COLUMN_NAME_SETTING);
                }
            } else {
                if (entityDescription.getTableName() == null) {
                    throw new RequiredSettingNotSetException(TABLE_NAME_SETTING);
                }
                if (entityDescription.getIdColumnName() == null) {
                    throw new RequiredSettingNotSetException(ID_COLUMN_NAME_SETTING);
                }
            }
            if (entityDescription.getTypeColumnName() != null) {
                throw new ExtraSettingFoundException(TYPE_COLUMN_NAME_SETTING);
            }
            if (entityDescription.getAggregateColumnName() != null) {
                throw new ExtraSettingFoundException(AGGREGATE_COLUMN_NAME_SETTING);
            }
            if (entityDescription.getSystemLocksTableName() != null) {
                throw new ExtraSettingFoundException(SYSTEM_LOCKS_TABLE_NAME_SETTING);
            }
            if (entityDescription.getSystemLocksAggregateColumnName() != null) {
                throw new ExtraSettingFoundException(SYSTEM_LOCKS_AGGREGATE_COLUMN_NAME_SETTING);
            }
            if (entityDescription.getSystemLocksVersionColumnName() != null) {
                throw new ExtraSettingFoundException(SYSTEM_LOCKS_VERSION_COLUMN_NAME_SETTING);
            }
        }
        entityDescription.getParamDescriptions().values().forEach(paramDescription -> {
            if (paramDescription.getType() == DataType.TEXT || paramDescription.getType() == DataType.BYTE_ARRAY) {
                throw new InvalidParamTypeException(paramDescription.getName(), paramDescription.getType());
            }
        });
        forEach(entityDescription.getDeclaredPrimitiveDescriptions().values().stream(), this::checkPropertyDescription, CheckPropertyDescriptionException::new);
        forEach(entityDescription.getDeclaredPrimitivesCollectionDescriptions().values().stream(), this::checkCollectionDescription, CheckPropertyDescriptionException::new);
        forEach(entityDescription.getDeclaredReferenceDescriptions().values().stream(), this::checkPropertyDescription, CheckPropertyDescriptionException::new);
        forEach(entityDescription.getDeclaredReferencesCollectionDescriptions().values().stream(), this::checkCollectionDescription, CheckPropertyDescriptionException::new);
        forEach(entityDescription.getDeclaredGroupDescriptions().values().stream(), groupDescription -> {
            forEach(groupDescription.getPrimitiveDescriptions().values().stream(), this::checkPropertyDescription, CheckPropertyDescriptionException::new);
            forEach(groupDescription.getReferenceDescriptions().values().stream(), this::checkPropertyDescription, CheckPropertyDescriptionException::new);
        }, CheckPropertyDescriptionException::new);
    }

    @Override
    public String getDescription() {
        return "Checking model description for access to entities for reading through JSON";
    }

    @Override
    public void check(ModelDescription modelDescription) {
        super.check(modelDescription);
        forEach(modelDescription.getEntityDescriptions().values().stream(), this::checkEntityDescription, CheckEntityDescriptionException::new);
        modelDescription.getGroupDescriptions().values().stream()
                .map(groupDescriptions -> groupDescriptions.get(0))
                .forEach(groupDescription -> {
                    if (groupDescription.getPrimitiveDescriptions().isEmpty() && groupDescription.getReferenceDescriptions().isEmpty()) {
                        throw new GroupPropertiesNotFoundException(groupDescription.getGroupName());
                    }
                });
    }
}
