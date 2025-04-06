package sbp.com.sbt.dataspace.feather.modeldescriptionimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescriptionCheck;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.*;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.wrapR;

public class ModelDescriptionConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelDescriptionConfiguration.class);

    /**
     * Get model description XML
     *
     * @param modelResourceName The name of the resource with the model
     */
    ModelDescriptionXml getModelDescriptionXml(String modelResourceName) {
        checkNotNull(modelResourceName, "Model resource name cannot be null");
        return wrapR(() -> ModelDescriptionConfiguration.class.getResourceAsStream(modelResourceName), inputStream -> {
            if (inputStream == null) {
                throw new ResourceNotFoundException();
            }
            JAXBContext context = JAXBContext.newInstance(ModelDescriptionXml.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (ModelDescriptionXml) unmarshaller.unmarshal(inputStream);
        }, throwable -> new ParseModelDescriptionException(throwable, modelResourceName));
    }

    /**
     * Get default value
     *
     * @param type        Тип
     * @param stringValue String value
     */
    Object getDefaultValue(DataType type, String stringValue) {
        if (stringValue == null) {
            return null;
        } else if (type == DataType.CHARACTER) {
            return stringValue.charAt(0);
        } else if (type == DataType.STRING) {
            return stringValue;
        } else if (type == DataType.BYTE) {
            return Byte.valueOf(stringValue);
        } else if (type == DataType.SHORT) {
            return Short.valueOf(stringValue);
        } else if (type == DataType.INTEGER) {
            return Integer.valueOf(stringValue);
        } else if (type == DataType.LONG) {
            return Long.valueOf(stringValue);
        } else if (type == DataType.FLOAT) {
            return Float.valueOf(stringValue);
        } else if (type == DataType.DOUBLE) {
            return Double.valueOf(stringValue);
        } else if (type == DataType.BIG_DECIMAL) {
            return new BigDecimal(stringValue);
        } else if (type == DataType.DATE) {
            return LocalDate.parse(stringValue, DateTimeFormatter.ISO_LOCAL_DATE);
        } else if (type == DataType.DATETIME) {
            return LocalDateTime.parse(stringValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } else if (type == DataType.OFFSET_DATETIME) {
            return OffsetDateTime.parse(stringValue, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else /* DateType.BOOLEAN */ {
            return Boolean.valueOf(stringValue);
        }
    }

    /**
     * Get model description settings
     *
     * @param modelDescriptionXml XML description of the model
     */
    sbp.com.sbt.dataspace.feather.modeldescriptioncommon.ModelDescriptionSettings getModelDescriptionSettings(ModelDescriptionXml modelDescriptionXml) {
        sbp.com.sbt.dataspace.feather.modeldescriptioncommon.ModelDescriptionSettings result = new sbp.com.sbt.dataspace.feather.modeldescriptioncommon.ModelDescriptionSettings();
        modelDescriptionXml.enumDescriptions.forEach(enumDescriptionXml -> result.setEnumDescriptionSettings(enumDescriptionXml.name, new EnumDescriptionSettings()
            .setValues(enumDescriptionXml.values)));
        modelDescriptionXml.entityDescriptions.forEach(entityDescriptionXml -> {
            EntityDescriptionSettings entityDescriptionSettings = new EntityDescriptionSettings()
                .setParentEntityType(entityDescriptionXml.parentEntityType)
                .setAggregateEntityType(entityDescriptionXml.aggregateEntityType)
                .setInheritanceStrategy(entityDescriptionXml.inheritanceStrategy)
                .setTableType(entityDescriptionXml.tableType)
                .setTableName(entityDescriptionXml.tableName)
                .setIdColumnName(entityDescriptionXml.idColumnName)
                .setTypeColumnName(entityDescriptionXml.typeColumnName)
                .setAggregateColumnName(entityDescriptionXml.aggregateColumnName)
                .setSystemLocksTableName(entityDescriptionXml.systemLocksTableName)
                .setSystemLocksAggregateColumnName(entityDescriptionXml.systemLocksAggregateColumnName)
                .setSystemLocksVersionColumnName(entityDescriptionXml.systemLocksVersionColumnName);
            if (entityDescriptionXml.final0) {
                entityDescriptionSettings.setFinal();
            }
            if (entityDescriptionXml.aggregate) {
                entityDescriptionSettings.setAggregate();
            }
            entityDescriptionXml.paramDescriptions.forEach(paramDescriptionXml -> {
                ParamDescriptionSettings paramDescriptionSettings = new ParamDescriptionSettings()
                    .setType(paramDescriptionXml.type)
                    .setDefaultValue(getDefaultValue(paramDescriptionXml.type, paramDescriptionXml.defaultValue));
                if (paramDescriptionXml.collection) {
                    paramDescriptionSettings.setCollection();
                }
                entityDescriptionSettings.setParamDescriptionSettings(paramDescriptionXml.name, paramDescriptionSettings);
            });
            entityDescriptionXml.primitiveDescriptions.forEach(primitiveDescriptionXml -> {
                PrimitiveDescriptionSettings primitiveDescriptionSettings = new PrimitiveDescriptionSettings()
                    .setColumnName(primitiveDescriptionXml.columnName)
                    .setType(primitiveDescriptionXml.type)
                    .setEnumType(primitiveDescriptionXml.enumType);
                if (primitiveDescriptionXml.mandatory) {
                    primitiveDescriptionSettings.setMandatory();
                }
                entityDescriptionSettings.setPrimitiveDescriptionSettings(primitiveDescriptionXml.name, primitiveDescriptionSettings);
            });
            entityDescriptionXml.primitivesCollectionDescriptions.forEach(primitivesCollectionDescriptionXml -> entityDescriptionSettings.setPrimitivesCollectionDescriptionSettings(primitivesCollectionDescriptionXml.name, new PrimitivesCollectionDescriptionSettings()
                .setColumnName(primitivesCollectionDescriptionXml.columnName)
                .setTableName(primitivesCollectionDescriptionXml.tableName)
                .setOwnerColumnName(primitivesCollectionDescriptionXml.ownerColumnName)
                .setType(primitivesCollectionDescriptionXml.type)
                .setEnumType(primitivesCollectionDescriptionXml.enumType)));
            entityDescriptionXml.referenceDescriptions.forEach(referenceDescriptionXml -> {
                ReferenceDescriptionSettings referenceDescriptionSettings = new ReferenceDescriptionSettings()
                    .setColumnName(referenceDescriptionXml.columnName)
                    .setEntityType(referenceDescriptionXml.entityType)
                    .setEntityReferencePropertyName(referenceDescriptionXml.entityReferencePropertyName)
                    .setEntityReferencesCollectionPropertyName(referenceDescriptionXml.entityReferencesCollectionPropertyName);
                if (referenceDescriptionXml.mandatory) {
                    referenceDescriptionSettings.setMandatory();
                }
                entityDescriptionSettings.setReferenceDescriptionSettings(referenceDescriptionXml.name, referenceDescriptionSettings);
            });
            entityDescriptionXml.referencesCollectionDescriptions.forEach(referencesCollectionDescriptionXml -> entityDescriptionSettings.setReferencesCollectionDescriptionSettings(referencesCollectionDescriptionXml.name, new ReferencesCollectionDescriptionSettings()
                .setColumnName(referencesCollectionDescriptionXml.columnName)
                .setTableName(referencesCollectionDescriptionXml.tableName)
                .setOwnerColumnName(referencesCollectionDescriptionXml.ownerColumnName)
                .setEntityType(referencesCollectionDescriptionXml.entityType)));
            entityDescriptionXml.groupDescriptions.forEach(groupDescriptionXml -> {
                GroupDescriptionSettings groupDescriptionSettings = new GroupDescriptionSettings();
                groupDescriptionSettings.setGroupName(groupDescriptionXml.groupName);
                groupDescriptionXml.primitiveDescriptions.forEach(primitiveDescriptionXml -> {
                    PrimitiveDescriptionSettings primitiveDescriptionSettings = new PrimitiveDescriptionSettings()
                        .setColumnName(primitiveDescriptionXml.columnName)
                        .setType(primitiveDescriptionXml.type)
                        .setEnumType(primitiveDescriptionXml.enumType);
                    if (primitiveDescriptionXml.mandatory) {
                        primitiveDescriptionSettings.setMandatory();
                    }
                    groupDescriptionSettings.setPrimitiveDescriptionSettings(primitiveDescriptionXml.name, primitiveDescriptionSettings);
                });
                groupDescriptionXml.referenceDescriptions.forEach(referenceDescriptionXml -> {
                    ReferenceDescriptionSettings referenceDescriptionSettings = new ReferenceDescriptionSettings()
                        .setColumnName(referenceDescriptionXml.columnName)
                        .setEntityType(referenceDescriptionXml.entityType)
                        .setEntityReferencePropertyName(referenceDescriptionXml.entityReferencePropertyName)
                        .setEntityReferencesCollectionPropertyName(referenceDescriptionXml.entityReferencesCollectionPropertyName);
                    if (referenceDescriptionXml.mandatory) {
                        referenceDescriptionSettings.setMandatory();
                    }
                    groupDescriptionSettings.setReferenceDescriptionSettings(referenceDescriptionXml.name, referenceDescriptionSettings);
                });
                entityDescriptionSettings.setGroupDescriptionSettings(groupDescriptionXml.name, groupDescriptionSettings);
            });
            result.setEntityDescriptionSettings(entityDescriptionXml.name, entityDescriptionSettings);
        });
        return result;
    }

    @Bean
    public ModelDescription modelDescription(ModelDescriptionSettings modelDescriptionSettings, List<ModelDescriptionCheck> modelDescriptionChecks) {
        LOGGER.info("{}", modelDescriptionSettings);
        ModelDescriptionXml modelDescriptionXml = getModelDescriptionXml(modelDescriptionSettings.modelResourceName);
        return new ModelDescriptionImpl(getModelDescriptionSettings(modelDescriptionXml), modelDescriptionChecks);
    }
}
