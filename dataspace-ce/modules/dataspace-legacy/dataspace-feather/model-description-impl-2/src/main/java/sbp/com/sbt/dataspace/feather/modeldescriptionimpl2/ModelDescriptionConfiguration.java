package sbp.com.sbt.dataspace.feather.modeldescriptionimpl2;

import com.sbt.dataspace.pdm.PdmModel;
import com.sbt.mg.data.model.ClassStrategy;
import com.sbt.mg.data.model.XmlEmbeddedList;
import com.sbt.mg.data.model.XmlEnumValue;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.parameters.enums.Changeable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.InheritanceStrategy;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescriptionCheck;
import sbp.com.sbt.dataspace.feather.modeldescription.TableType;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.EntityDescriptionSettings;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.EnumDescriptionSettings;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.GroupDescriptionSettings;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.ModelDescriptionImpl;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.ParamDescriptionSettings;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.PrimitiveDescriptionSettings;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.PrimitivesCollectionDescriptionSettings;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.ReferenceDescriptionSettings;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;

public class ModelDescriptionConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(ModelDescriptionConfiguration.class);
  static final Map<String, DataType> TYPES_MAPPING;
  static final Map<ClassStrategy, InheritanceStrategy> INHERITANCE_STRATEGY_MAPPING;

    /**
     * Get entity description settings
     *
     * @param modelDescriptionSettings Model description settings
     * @param entityType               Entity type
     */
  EntityDescriptionSettings getEntityDescriptionSettings(sbp.com.sbt.dataspace.feather.modeldescriptioncommon.ModelDescriptionSettings modelDescriptionSettings, String entityType) {
    EntityDescriptionSettings result = modelDescriptionSettings.getEntityDescriptionSettings(entityType);
    if (result == null) {
      result = new EntityDescriptionSettings();
      modelDescriptionSettings.setEntityDescriptionSettings(entityType, result);
    }
    return result;
  }

    /**
     * Get link description settings
     *
     * @param entityDescriptionSettings The entity description settings
     * @param propertyName              The name of the property
     */
  ReferenceDescriptionSettings getReferenceDescriptionSettings(EntityDescriptionSettings entityDescriptionSettings, String propertyName) {
    ReferenceDescriptionSettings result = entityDescriptionSettings.getReferenceDescriptionSettings(propertyName);
    if (result == null) {
      result = new ReferenceDescriptionSettings();
      entityDescriptionSettings.setReferenceDescriptionSettings(propertyName, result);
    }
    return result;
  }

    /**
     * Get type
     *
     * @param xmlModelClassProperty XML properties of the model class
     */
  DataType getType(XmlModelClassProperty xmlModelClassProperty) {
    DataType result = TYPES_MAPPING.get(xmlModelClassProperty.getType());
    if (result == DataType.STRING && xmlModelClassProperty.getLength() > 4000) {
      result = DataType.TEXT;
    }
    return result;
  }

    /**
     * Get XML of parent class model
     *
     * @param xmlModelClass XML model class
     */
  XmlModelClass getParentXmlModelClass(XmlModelClass xmlModelClass) {
    //If the class has no parent, then null is returned
    return xmlModelClass.getExtendedClass();
  }

    /**
     * Get XML of root class model
     *
     * @param xmlModelClass XML model class
     */
  XmlModelClass getRootXmlModelClass(XmlModelClass xmlModelClass) {
    XmlModelClass result;
    XmlModelClass parent = xmlModelClass;
    do {
      result = parent;
      do {
        parent = getParentXmlModelClass(parent);
      } while (parent != null && parent.isAbstract());
    } while (parent != null);
    return result;
  }

    /**
     * Add configuration properties grouping
     *
     * @param groupDescriptionSettings Grouping description settings
     * @param embeddableXmlModelClass     XML of the embedded model class
     * @param xmlEmbeddedList             XML list of embedded properties
     * @param externalLink                Is it an external link?
     */
  void addGroupPropertyDescriptionSettings(GroupDescriptionSettings groupDescriptionSettings, XmlModelClass embeddableXmlModelClass, XmlEmbeddedList xmlEmbeddedList, boolean externalLink) {
    groupDescriptionSettings.setGroupName(embeddableXmlModelClass.getName());
    embeddableXmlModelClass.getPropertiesAsList().forEach(xmlModelClassProperty -> {
      DataType type = xmlModelClassProperty.isEnum() ? DataType.STRING : getType(xmlModelClassProperty);
      String columnName = xmlEmbeddedList.getEmbeddedPropertyList().stream().filter(xmlEmbeddedProperty -> xmlEmbeddedProperty.getName().equals(xmlModelClassProperty.getName())).findFirst().get().getColumnName();
      PrimitiveDescriptionSettings primitiveDescriptionSettings = new PrimitiveDescriptionSettings()
        .setColumnName(columnName)
        .setType(type);
      if (xmlModelClassProperty.isMandatory()) {
        primitiveDescriptionSettings.setMandatory();
      }
      if (xmlModelClassProperty.isEnum()) {
        primitiveDescriptionSettings.setEnumType(xmlModelClassProperty.getType());
      }
      groupDescriptionSettings.setPrimitiveDescriptionSettings(xmlModelClassProperty.getName(), primitiveDescriptionSettings);
      if (externalLink && "entityId".equals(xmlModelClassProperty.getName())) {
        String entityType = embeddableXmlModelClass.getName().substring(0, embeddableXmlModelClass.getName().lastIndexOf("Reference"));
        if (embeddableXmlModelClass.getModel().containsClass(entityType)) {
          groupDescriptionSettings.setReferenceDescriptionSettings("entity", new ReferenceDescriptionSettings()
            .setColumnName(columnName)
            .setEntityType(entityType));
        }
      }
    });
  }

    /**
     * Add property descriptions settings
     *
     * @param modelDescriptionSettings  Model description settings
     * @param entityDescriptionSettings The entity description settings
     * @param pdmModel                  Pdm file model
     * @param xmlModelClass             XML model class
     */
  void addPropertyDescriptionSettings(sbp.com.sbt.dataspace.feather.modeldescriptioncommon.ModelDescriptionSettings modelDescriptionSettings, EntityDescriptionSettings entityDescriptionSettings, PdmModel pdmModel, XmlModelClass xmlModelClass) {
    xmlModelClass.getPropertiesAsList().forEach(xmlModelClassProperty -> {
      if (Boolean.TRUE.equals(xmlModelClassProperty.isId())) {
        entityDescriptionSettings.setIdColumnName(xmlModelClassProperty.getColumnName());
      } else if (xmlModelClassProperty.getChangeable() != Changeable.SYSTEM || xmlModelClass.getName().endsWith("ApiCall")) {
        if (xmlModelClassProperty.isEmbedded()) {
          GroupDescriptionSettings groupDescriptionSettings = new GroupDescriptionSettings();
          addGroupPropertyDescriptionSettings(groupDescriptionSettings, xmlModelClass.getModel().getClass(xmlModelClassProperty.getType()), xmlModelClass.getEmbeddedPropertyList().stream().filter(xmlEmbeddedList -> xmlEmbeddedList.getName().equals(xmlModelClassProperty.getName())).findFirst().get(), xmlModelClassProperty.isExternalLink());
          entityDescriptionSettings.setGroupDescriptionSettings(xmlModelClassProperty.getName(), groupDescriptionSettings);
        } else {
          DataType type = xmlModelClassProperty.isEnum() ? DataType.STRING : getType(xmlModelClassProperty);
          if (type != null) {
            if (xmlModelClassProperty.getCollectionType() == null) {
              if ("type".equals(xmlModelClassProperty.getName()) && !xmlModelClass.isFinalClass()) {
                entityDescriptionSettings.setTypeColumnName(xmlModelClassProperty.getColumnName());
              }
              PrimitiveDescriptionSettings primitiveDescriptionSettings = new PrimitiveDescriptionSettings()
                .setColumnName(xmlModelClassProperty.getColumnName())
                .setType(type);
              if (xmlModelClassProperty.isMandatory()) {
                primitiveDescriptionSettings.setMandatory();
              }
              if (xmlModelClassProperty.isEnum()) {
                primitiveDescriptionSettings.setEnumType(xmlModelClassProperty.getType());
              }
              entityDescriptionSettings.setPrimitiveDescriptionSettings(xmlModelClassProperty.getName(), primitiveDescriptionSettings);
            } else {
              PrimitivesCollectionDescriptionSettings primitivesCollectionDescriptionSettings = new PrimitivesCollectionDescriptionSettings()
                .setColumnName(xmlModelClassProperty.getColumnName())
                .setTableName(xmlModelClassProperty.getCollectionTableName())
                .setOwnerColumnName(xmlModelClassProperty.getKeyColumnName())
                .setType(type);
              if (xmlModelClassProperty.isEnum()) {
                primitivesCollectionDescriptionSettings.setEnumType(xmlModelClassProperty.getType());
              }
              entityDescriptionSettings.setPrimitivesCollectionDescriptionSettings(xmlModelClassProperty.getName(), primitivesCollectionDescriptionSettings);
            }
          } else {
            if (xmlModelClassProperty.getCollectionType() == null) {
              if (xmlModelClassProperty.getMappedBy() == null) {
                ReferenceDescriptionSettings referenceDescriptionSettings = getReferenceDescriptionSettings(entityDescriptionSettings, xmlModelClassProperty.getName())
                  .setColumnName(xmlModelClassProperty.getColumnName())
                  .setEntityType(xmlModelClassProperty.getType());
                                /*

                                  The problem: When requesting history data along with the historical object data (through history), provided that the object is deleted, the join optimization is triggered, resulting in no deleted object data being retrieved.

                                  The decision: At the level of forming the metamodel, do not set mandatory for the field sysHistoryOwner.
                                 */
                final boolean isHistoryOwnerField = xmlModelClass.isHistoryClass() && JpaConstants.HISTORY_OWNER_PROPERTY.equals(xmlModelClassProperty.getName());
                if (!isHistoryOwnerField && xmlModelClassProperty.isMandatory()) {
                  referenceDescriptionSettings.setMandatory();
                }
              } else {
                getReferenceDescriptionSettings(getEntityDescriptionSettings(modelDescriptionSettings, xmlModelClassProperty.getType()), xmlModelClassProperty.getMappedBy())
                  .setEntityReferencePropertyName(xmlModelClassProperty.getName());
              }
            } else {
              getReferenceDescriptionSettings(getEntityDescriptionSettings(modelDescriptionSettings, xmlModelClassProperty.getType()), xmlModelClassProperty.getMappedBy())
                .setEntityReferencesCollectionPropertyName(xmlModelClassProperty.getName());
            }
          }
// TODO: 28.08.2024 Disabling work with aggregates, since there is no system-locks in pdm.
//                    if ("aggregateRoot".equals(xmlModelClassProperty.getName())) {
//                        entityDescriptionSettings.setAggregateEntityType(getRootXmlModelClass(pdmModel.getModel().getClass(xmlModelClassProperty.getType())).getName());
//                        entityDescriptionSettings.setAggregateColumnName(xmlModelClassProperty.getColumnName());
//                    }
        }
      }
    });
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
     * @param pdmModel PDM file model
     */
  sbp.com.sbt.dataspace.feather.modeldescriptioncommon.ModelDescriptionSettings getModelDescriptionSettings(PdmModel pdmModel) {
    sbp.com.sbt.dataspace.feather.modeldescriptioncommon.ModelDescriptionSettings result = new sbp.com.sbt.dataspace.feather.modeldescriptioncommon.ModelDescriptionSettings();
    pdmModel.getModel().getEnums().forEach(xmlModelClassEnum -> result.setEnumDescriptionSettings(xmlModelClassEnum.getName(), new EnumDescriptionSettings()
      .setValues(xmlModelClassEnum.getEnumValues().stream().map(XmlEnumValue::getName).collect(Collectors.toSet()))));
    Predicate<XmlModelClass> predicate = xmlModelClass -> !(Boolean.TRUE.equals(xmlModelClass.isAbstract()) || xmlModelClass.isEmbeddable() || Changeable.SYSTEM == xmlModelClass.getClassAccess()) || xmlModelClass.getName().endsWith("ApiCall");
    pdmModel.getModel().getClassesAsList().stream()
      .filter(predicate)
      .forEach(xmlModelClass -> {
        EntityDescriptionSettings entityDescriptionSettings = getEntityDescriptionSettings(result, xmlModelClass.getName());
        if (Boolean.TRUE.equals(xmlModelClass.isFinalClass())) {
          entityDescriptionSettings.setFinal();
        }

        XmlModelClass parent = getParentXmlModelClass(xmlModelClass);
        while (parent != null && parent.isAbstract()) {
          addPropertyDescriptionSettings(result, entityDescriptionSettings, pdmModel, parent);
          parent = getParentXmlModelClass(parent);
        }
        if (parent != null) {
          entityDescriptionSettings.setParentEntityType(parent.getName());
        } else {
          entityDescriptionSettings.setInheritanceStrategy(INHERITANCE_STRATEGY_MAPPING.get(xmlModelClass.getStrategy()));
        }
        addPropertyDescriptionSettings(result, entityDescriptionSettings, pdmModel, xmlModelClass);
      });
    pdmModel.getModel().getClassesAsList().stream()
      .filter(predicate)
      .forEach(xmlModelClass -> {
        EntityDescriptionSettings entityDescriptionSettings = result.getEntityDescriptionSettings(xmlModelClass.getName());
        EntityDescriptionSettings rootEntityDescriptionSettings = entityDescriptionSettings;
        while (rootEntityDescriptionSettings.getParentEntityType() != null) {
          rootEntityDescriptionSettings = result.getEntityDescriptionSettings(rootEntityDescriptionSettings.getParentEntityType());
        }
        if (rootEntityDescriptionSettings.getInheritanceStrategy() != InheritanceStrategy.SINGLE_TABLE) {
          entityDescriptionSettings.setIdColumnName(rootEntityDescriptionSettings.getIdColumnName());
        }
        if (rootEntityDescriptionSettings.getInheritanceStrategy() != InheritanceStrategy.SINGLE_TABLE || rootEntityDescriptionSettings == entityDescriptionSettings) {
          entityDescriptionSettings.setTableName(xmlModelClass.getTableName());
        }
      });
// TODO: 28.08.2024 Disabling work with aggregates, since there is no system-locks in pdm
//        pdmModel.getSystemLocksModel().getSystemLocksClasses().values().forEach(xmlSystemLocksClasses -> {
//            EntityDescriptionSettings entityDescriptionSettings = result.getEntityDescriptionSettings(xmlSystemLocksClasses.getSystemLocksClass());
//            if (entityDescriptionSettings != null) {
//                entityDescriptionSettings
//                        .setAggregate()
//                        .setSystemLocksTableName(xmlSystemLocksClasses.getSystemLocksTableName())
//                        .setSystemLocksAggregateColumnName("ROOTID")
//                        .setSystemLocksVersionColumnName("VERSION");
//            }
//        });
    pdmModel.getModel().getQueriesAsList().forEach(query -> {
      EntityDescriptionSettings entityDescriptionSettings = new EntityDescriptionSettings()
        .setFinal()
        .setInheritanceStrategy(InheritanceStrategy.JOINED)
        .setTableType(TableType.QUERY);
      if (query.getId() != null) {
        entityDescriptionSettings.setIdColumnName(query.getId().getName());
      }
      query.getParams().forEach(param -> {
        DataType type = TYPES_MAPPING.get(param.getType());
        ParamDescriptionSettings paramDescriptionSettings = new ParamDescriptionSettings()
          .setType(type)
.setDefaultValue(getDefaultValue(type, param.getDefaultValue()));
        if (param.getCollection())
          paramDescriptionSettings.setCollection();
        entityDescriptionSettings.setParamDescriptionSettings(param.getName(), paramDescriptionSettings);
      });
      query.getProperties().forEach(property -> entityDescriptionSettings.setPrimitiveDescriptionSettings(property.getName(), new PrimitiveDescriptionSettings()
        .setColumnName(property.getName())
        .setType(TYPES_MAPPING.get(property.getType()))));
      result.setEntityDescriptionSettings(query.getName(), entityDescriptionSettings);
    });
    return result;
  }

  @Bean
  public ModelDescription modelDescription(ModelDescriptionSettings modelDescriptionSettings, List<ModelDescriptionCheck> modelDescriptionChecks) {
    LOGGER.info("{}", modelDescriptionSettings);
    checkNotNull(modelDescriptionSettings.pdmModel, "XML model");
    return new ModelDescriptionImpl(getModelDescriptionSettings(modelDescriptionSettings.pdmModel), modelDescriptionChecks);
  }

  static {
    TYPES_MAPPING = new HashMap<>();
    TYPES_MAPPING.put("Character", DataType.CHARACTER);
    TYPES_MAPPING.put("String", DataType.STRING);
    TYPES_MAPPING.put("Byte", DataType.BYTE);
    TYPES_MAPPING.put("Short", DataType.SHORT);
    TYPES_MAPPING.put("Integer", DataType.INTEGER);
    TYPES_MAPPING.put("Long", DataType.LONG);
    TYPES_MAPPING.put("Float", DataType.FLOAT);
    TYPES_MAPPING.put("Double", DataType.DOUBLE);
    TYPES_MAPPING.put("BigDecimal", DataType.BIG_DECIMAL);
    TYPES_MAPPING.put("LocalDate", DataType.DATE);
    TYPES_MAPPING.put("LocalDateTime", DataType.DATETIME);
    TYPES_MAPPING.put("OffsetDateTime", DataType.OFFSET_DATETIME);
    TYPES_MAPPING.put("Date", DataType.DATETIME);
    TYPES_MAPPING.put("Boolean", DataType.BOOLEAN);
    TYPES_MAPPING.put("byte[]", DataType.BYTE_ARRAY);

    INHERITANCE_STRATEGY_MAPPING = new EnumMap<>(ClassStrategy.class);
    INHERITANCE_STRATEGY_MAPPING.put(ClassStrategy.SINGLE_TABLE, InheritanceStrategy.SINGLE_TABLE);
    INHERITANCE_STRATEGY_MAPPING.put(ClassStrategy.JOINED, InheritanceStrategy.JOINED);
  }
}
