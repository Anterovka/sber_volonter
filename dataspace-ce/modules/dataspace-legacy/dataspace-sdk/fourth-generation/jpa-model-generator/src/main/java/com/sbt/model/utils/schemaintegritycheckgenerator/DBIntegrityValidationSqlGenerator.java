package com.sbt.model.utils.schemaintegritycheckgenerator;

import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.ClassStrategy;
import com.sbt.mg.data.model.PropertyType;
import com.sbt.mg.data.model.XmlEmbeddedList;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.usermodel.UserXmlModelClassProperty;
import com.sbt.reference.ExternalReferenceGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sbt.mg.jpa.JpaConstants.AGGREGATE_ROOT;
import static com.sbt.mg.utils.ClassUtils.isBaseClass;

public class DBIntegrityValidationSqlGenerator {

    /**
     * ignoreHistoryClasses - flag to disable historical classes in selection.
     * At the initial stage, this flag is default set to true.
     * In the future, it may be displayed in the plugin generation parameters (or it may be deleted).
     */
    private final boolean ignoreHistoryClasses = true;
    private final List<String> listSql = new ArrayList<>();
    private final String SYS_OWNERID = "sys_ownerid";

    public String generate(XmlModel xmlModel) {

        DBIntegrityCheckType[] types = DBIntegrityCheckType.values();
        for (DBIntegrityCheckType type : types) {
            generateScripts(type, xmlModel);
        }

        return String.join(" union all\n", listSql);
    }

    private void generateScripts(DBIntegrityCheckType type, XmlModel xmlModel) {
        switch (type) {
            case PNUL:
            case PMIS:
                generateSqlForCheckParent(type, xmlModel);
                break;
            case ANUL:
            case AMIS:
            case OWDI:
                generateSqlForCheckAggregateRoot(type, xmlModel);
                break;
            case OMIS:
                generateSqlForCheckCollectionOwner(type, xmlModel);
                break;
            case JANC:
                generateSqlForCheckAncestor(type, xmlModel);
                break;
            case JDES:
                generateSqlForCheckDescendant(type, xmlModel);
                break;
            case FMIS:
            case FABV:
                generateSqlForCheckProperty(type, xmlModel);
                break;
            case RMIS:
                generateSqlForCheckReference(type, xmlModel);
                break;
            case RABV:
                generateSqlForCheckReferenceToChildAgg(type, xmlModel);
                break;
            case MNUL:
                generateSqlForCheckMandatory(type, xmlModel);
                break;
            case OWEM:
                generateSqlForCheckAggregateRootOwnerId(type, xmlModel);
                break;
        }
    }

    /**
* Checking that entities which are child elements of an aggregate by model should have:
*  - parent filled (PNUL)
* - parent exists (PMIС)
     *
     * @param type
     * @param xmlModel
     */
    private void generateSqlForCheckParent(DBIntegrityCheckType type, XmlModel xmlModel) {
        Stream<XmlModelClass> streamModelClassesWithParent = xmlModel.getClassesAsList().stream()
                .filter(xmlModelClass -> Objects.nonNull(xmlModelClass.getTableName()))
                .filter(xmlModelClass -> !xmlModelClass.isEmbeddable())
                .filter(xmlModelClass -> xmlModelClass.getPropertiesAsList().stream()
                        .anyMatch(XmlModelClassProperty::isParent));
// Excluding historical classes, as they may store references to already deleted entities
        if (ignoreHistoryClasses && type == DBIntegrityCheckType.PMIS) {
            streamModelClassesWithParent = streamModelClassesWithParent.filter(xmlModelClass -> !xmlModelClass.isHistoryClass());
        }
        List<XmlModelClass> modelClassesWithParent = streamModelClassesWithParent
                .collect(Collectors.toList());

        modelClassesWithParent.forEach(xmlModelClass -> {
            xmlModelClass.getPropertiesAsList().stream()
                    .filter(XmlModelClassProperty::isParent)
                    .filter(property -> Objects.nonNull(property.getColumnName()))
                    .filter(property -> !xmlModelClass.getName().equals(property.getType()))
                    .forEach(property -> {
                        XmlModelClass modelClassParent = xmlModelClass.getModel().getClass(property.getType());
                        if (Objects.nonNull(modelClassParent.getTableName())) {

                            if (type == DBIntegrityCheckType.PNUL) {
                                IntegrityCheckReturnedColumns returnedColumns =
                                        new IntegrityCheckReturnedColumns(xmlModelClass.getName(), "object_id",
                                                xmlModelClass.getTableName(), property.getColumnName(),
                                                String.format("t1.%s", property.getColumnName()), "",
                                                "", "''", type.getCheckType(),
                                                type.getErrorCode(), type.getErrorDetails());

                                String sql = String.format(type.getSqlQuery(), returnedColumns, xmlModelClass.getTableName(), property.getColumnName());
                                listSql.add(sql);

                            } else if (type == DBIntegrityCheckType.PMIS) {
                                IntegrityCheckReturnedColumns returnedColumns =
                                        new IntegrityCheckReturnedColumns(xmlModelClass.getName(), "object_id",
                                                xmlModelClass.getTableName(), property.getColumnName(),
                                                String.format("t1.%s", property.getColumnName()), modelClassParent.getTableName(),
                                                "object_id", "''", type.getCheckType(),
                                                type.getErrorCode(), String.format(type.getErrorDetails(), modelClassParent.getTableName()));
                                String sql = String.format(type.getSqlQuery(), returnedColumns, xmlModelClass.getTableName(), property.getColumnName(), modelClassParent.getTableName());
                                listSql.add(sql);
                            }
                        }
                    });
        });

    }

    /**
     * Checking that entities which are child elements of an aggregate by model should have:
     * - aggretaterootid filled (ANUL)
     * The text does not contain any Russian words or phrases to be translated into English. Therefore, no replacements are needed.
     * - correspondence of the ownerId of the aggregate root and child elements (OWDI)
     *
     * @param type
     * @param xmlModel
     */
    private void generateSqlForCheckAggregateRoot(DBIntegrityCheckType type, XmlModel xmlModel) {
        Stream<XmlModelClass> streamModelClassWithAggregateRootId = xmlModel.getClassesAsList().stream()
                .filter(xmlModelClass -> Objects.nonNull(xmlModelClass.getTableName()))
                .filter(xmlModelClass -> !xmlModelClass.isEmbeddable())
                .filter(xmlModelClass -> xmlModelClass.getPropertiesAsList().stream()
                        .anyMatch(property -> AGGREGATE_ROOT.equals(property.getName())));
        // Excluding historical classes, as they may store references to already deleted root aggregate entities
        if (ignoreHistoryClasses && type == DBIntegrityCheckType.AMIS) {
            streamModelClassWithAggregateRootId = streamModelClassWithAggregateRootId.filter(xmlModelClass -> !xmlModelClass.isHistoryClass());
        }
        List<XmlModelClass> modelClassesWithAggregateRootId = streamModelClassWithAggregateRootId
                .collect(Collectors.toList());

        modelClassesWithAggregateRootId.forEach(xmlModelClass -> {
            Optional<XmlModelClassProperty> optionalXmlModelClassProperty = xmlModelClass.getPropertiesAsList().stream()
                    .filter(property -> AGGREGATE_ROOT.equals(property.getName()))
                    .filter(property -> Objects.nonNull(property.getColumnName()))
                    .findFirst();
            if (optionalXmlModelClassProperty.isPresent()) {
                XmlModelClassProperty property = optionalXmlModelClassProperty.get();
                XmlModelClass modelClassAggregateRoot = xmlModelClass.getModel().getClass(property.getType());

                if (Objects.nonNull(modelClassAggregateRoot.getTableName())) {
                    if (type == DBIntegrityCheckType.ANUL) {
                        IntegrityCheckReturnedColumns returnedColumns =
                                new IntegrityCheckReturnedColumns(xmlModelClass.getName(), "object_id",
                                        xmlModelClass.getTableName(), property.getColumnName(),
                                        String.format("t1.%s", property.getColumnName()), "",
                                        "", "''", type.getCheckType(),
                                        type.getErrorCode(), type.getErrorDetails());
                        String sql = String.format(type.getSqlQuery(), returnedColumns, xmlModelClass.getTableName());
                        listSql.add(sql);

                    } else if (type == DBIntegrityCheckType.AMIS) {
                        IntegrityCheckReturnedColumns returnedColumns =
                                new IntegrityCheckReturnedColumns(xmlModelClass.getName(), "object_id",
                                        xmlModelClass.getTableName(), property.getColumnName(),
                                        String.format("t1.%s", property.getColumnName()), modelClassAggregateRoot.getTableName(),
                                        "object_id", "''", type.getCheckType(),
                                        type.getErrorCode(), String.format(type.getErrorDetails(), modelClassAggregateRoot.getTableName()));
                        String sql = String.format(type.getSqlQuery(), returnedColumns, xmlModelClass.getTableName(), modelClassAggregateRoot.getTableName());
                        listSql.add(sql);

                    } else if (type == DBIntegrityCheckType.OWDI) {
                        if (!xmlModelClass.isDictionary()) {

                            XmlModelClass modelClassAggregateRootWithOwnerId = null;

                            if (modelClassAggregateRoot.getPropertiesWithAllIncome().stream().anyMatch(xmlModelClassProperty -> SYS_OWNERID.equalsIgnoreCase(xmlModelClassProperty.getColumnName()))) {
                                modelClassAggregateRootWithOwnerId = modelClassAggregateRoot;
                            } else {
                                XmlModelClass extendedClass = modelClassAggregateRoot.getExtendedClass();
                                while (Objects.isNull(extendedClass) || !isBaseClass(extendedClass.getName())) {
                                    if (extendedClass.getPropertiesWithAllIncome().stream().anyMatch(xmlModelClassProperty -> SYS_OWNERID.equalsIgnoreCase(xmlModelClassProperty.getColumnName()))) {
                                        modelClassAggregateRootWithOwnerId = extendedClass;
                                    }
                                    extendedClass = extendedClass.getExtendedClass();
                                }
                            }

                            if (Objects.nonNull(modelClassAggregateRootWithOwnerId)
                                    && Objects.nonNull(modelClassAggregateRootWithOwnerId.getTableName())
                                    && modelClassAggregateRootWithOwnerId.getPropertiesWithAllIncome().stream().anyMatch(xmlModelClassProperty -> SYS_OWNERID.equalsIgnoreCase(xmlModelClassProperty.getColumnName()))
                                    && xmlModelClass.getPropertiesWithAllIncome().stream().anyMatch(xmlModelClassProperty -> SYS_OWNERID.equalsIgnoreCase(xmlModelClassProperty.getColumnName()))) {

                                IntegrityCheckReturnedColumns returnedColumns =
                                        new IntegrityCheckReturnedColumns(xmlModelClass.getName(), "object_id",
                                                xmlModelClass.getTableName(), SYS_OWNERID,
                                                String.format("t1.%s", SYS_OWNERID), modelClassAggregateRootWithOwnerId.getTableName(),
                                                SYS_OWNERID, String.format("t2.%s", SYS_OWNERID), type.getCheckType(),
                                                type.getErrorCode(), String.format(type.getErrorDetails(), modelClassAggregateRootWithOwnerId.getTableName()));
                                String sql = String.format(type.getSqlQuery(), returnedColumns, xmlModelClass.getTableName(), modelClassAggregateRootWithOwnerId.getTableName());
                                listSql.add(sql);
                            }
                        }
                    }

                }
            }
        });

    }

    /**
     * Checking backlink to the collection owner and entity existence by backlink
     *
     * @param type
     * @param xmlModel
     */
    private void generateSqlForCheckCollectionOwner(DBIntegrityCheckType type, XmlModel xmlModel) {
        List<XmlModelClass> modelClassesWithCollectionProp = xmlModel.getClassesAsList().stream()
                .filter(xmlModelClass -> Objects.nonNull(xmlModelClass.getTableName()))
                .filter(xmlModelClass -> !xmlModelClass.isEmbeddable())
                .filter(xmlModelClass -> xmlModelClass.getPropertiesAsList().stream()
                        .anyMatch(property -> Objects.nonNull(property.getCollectionType()) && property.getCategory() == PropertyType.PRIMITIVE))
                .collect(Collectors.toList());

        modelClassesWithCollectionProp.forEach(xmlModelClass -> {
            xmlModelClass.getPropertiesAsList().stream()
                    .filter(property -> Objects.nonNull(property.getCollectionType()) && property.getCategory() == PropertyType.PRIMITIVE)
                    .forEach(property -> {

                        IntegrityCheckReturnedColumns returnedColumns =
                                new IntegrityCheckReturnedColumns(xmlModelClass.getName(), property.getKeyColumnName(),
                                        property.getCollectionTableName(), property.getKeyColumnName(),
                                        String.format("t1.%s", property.getKeyColumnName()), xmlModelClass.getTableName(),
                                        "object_id", "''", type.getCheckType(),
                                        type.getErrorCode(), String.format(type.getErrorDetails(), xmlModelClass.getTableName()));
                        String sql = String.format(type.getSqlQuery(), returnedColumns, property.getCollectionTableName(), property.getKeyColumnName(), xmlModelClass.getTableName());
                        listSql.add(sql);
                    });
        });
    }

    /**
     * Checking the existence of a record in the main table (when using the JOINED strategy)
     *
     * @param type
     * @param xmlModel
     */
    private void generateSqlForCheckAncestor(DBIntegrityCheckType type, XmlModel xmlModel) {
        List<XmlModelClass> childrenModelClasses = xmlModel.getClassesAsList().stream()
                .filter(xmlModelClass -> Objects.nonNull(xmlModelClass.getTableName()))
                .filter(xmlModelClass -> !xmlModelClass.isEmbeddable())
                .filter(xmlModelClass -> xmlModelClass.getStrategy() == ClassStrategy.JOINED)
                .filter(xmlModelClass -> xmlModelClass.getExtendedClass() != null && !isBaseClass(xmlModelClass.getExtendedClass().getName()))
                .collect(Collectors.toList());


        childrenModelClasses.forEach(xmlModelClass -> {
            ModelHelper.getAllSuperClasses(xmlModelClass).stream()
                    .filter(superClass -> !superClass.isAbstract() && Objects.nonNull(superClass.getTableName()))
                    .forEach(superClass -> {

                        IntegrityCheckReturnedColumns returnedColumns =
                                new IntegrityCheckReturnedColumns(xmlModelClass.getName(), "object_id",
                                        xmlModelClass.getTableName(), "object_id",
                                        "t1.object_id", superClass.getTableName(),
                                        "object_id", "''", type.getCheckType(),
                                        type.getErrorCode(), String.format(type.getErrorDetails(), superClass.getTableName()));

                        String sql = String.format(type.getSqlQuery(), returnedColumns, xmlModelClass.getTableName(), superClass.getTableName());
                        listSql.add(sql);

                    });
        });

    }

    /**
     * Checking the existence of a record in child tables (with JOINED strategy)
     *
     * @param type
     * @param xmlModel
     */
    private void generateSqlForCheckDescendant(DBIntegrityCheckType type, XmlModel xmlModel) {
        List<XmlModelClass> baseModelClasses = xmlModel.getClassesAsList().stream()
                .filter(xmlModelClass -> Objects.nonNull(xmlModelClass.getTableName()))
                .filter(xmlModelClass -> !xmlModelClass.isEmbeddable())
                .filter(xmlModelClass -> xmlModelClass.getStrategy() == ClassStrategy.JOINED)
                .filter(XmlModelClass::isBaseClassMark)
                .collect(Collectors.toList());

        baseModelClasses.forEach(xmlModelClass -> {
            Set<XmlModelClass> childrenModelClasses = ModelHelper.getAllChildClasses(xmlModelClass);
            childrenModelClasses.stream()
                    .filter(childModelClass -> Objects.nonNull(childModelClass.getTableName()))
                    .forEach(childModelClass -> {
                        IntegrityCheckReturnedColumns returnedColumns =
                                new IntegrityCheckReturnedColumns(xmlModelClass.getName(), "object_id",
                                        xmlModelClass.getTableName(), "object_id",
                                        "t1.object_id", childModelClass.getTableName(),
                                        "object_id", "''", type.getCheckType(),
                                        type.getErrorCode(), String.format(type.getErrorDetails(), childModelClass.getTableName()));

                        List<String> childNames = ModelHelper.getAllChildClasses(childModelClass, true).stream()
                                .filter(modelClass -> Objects.nonNull(modelClass.getTableName()))
                                .map(modelClass -> String.format("\'%s\'", modelClass.getName()))
                                .collect(Collectors.toList());

                        String sql = String.format(type.getSqlQuery(), returnedColumns, xmlModelClass.getTableName(), childModelClass.getTableName(), String.join(", ", childNames));
                        listSql.add(sql);
                    });
        });
    }

    /**
     * Checking:
     * - all properties must refer to existing objects (FMIS)
     * The links within the aggregate must belong to one aggregate (FABV).
     *
     * @param type
     * @param xmlModel
     */
    private void generateSqlForCheckProperty(DBIntegrityCheckType type, XmlModel xmlModel) {
        List<XmlModelClassProperty> properties = new ArrayList<>();
        xmlModel.getClassesAsList().stream()
                .filter(xmlModelClass -> Objects.nonNull(xmlModelClass.getTableName()))
                .filter(xmlModelClass -> !xmlModelClass.isEmbeddable())
                .forEach(xmlModelClass -> xmlModelClass.getPropertiesAsList().stream()
                        .filter(property -> property.getCategory() == PropertyType.REFERENCE
                                && !property.isEmbedded()
                                && !property.isParent()
                                && Objects.nonNull(property.getColumnName())
                                && !AGGREGATE_ROOT.equals(property.getName())
                                && Objects.isNull(property.getCollectionType()))
                        .forEach(properties::add));

        properties.forEach(property -> {
            XmlModelClass modelClass = property.getModelClass();
            XmlModelClass otherModelClass = modelClass.getModel().getClass(property.getType());
            if (Objects.nonNull(otherModelClass.getTableName())) {

                if (type == DBIntegrityCheckType.FMIS) {
                    IntegrityCheckReturnedColumns returnedColumns =
                            new IntegrityCheckReturnedColumns(modelClass.getName(), "object_id",
                                    modelClass.getTableName(), property.getColumnName(),
                                    String.format("t1.%s", property.getColumnName()), otherModelClass.getTableName(),
                                    "object_id", "''", type.getCheckType(),
                                    type.getErrorCode(), String.format(type.getErrorDetails(), otherModelClass.getTableName()));

                    String sql = String.format(type.getSqlQuery(), returnedColumns, modelClass.getTableName(), property.getColumnName(), otherModelClass.getTableName());
                    listSql.add(sql);
                } else if (type == DBIntegrityCheckType.FABV) {

                    boolean modelClassIsRoot = modelClass.getPropertiesAsList().stream().noneMatch(prop -> AGGREGATE_ROOT.equals(prop.getName()));
                    boolean otherModelClassIsRoot = otherModelClass.getPropertiesAsList().stream().noneMatch(prop -> AGGREGATE_ROOT.equals(prop.getName()));

                    // first exclude checking the aggregate when referring to an entity from a reference book
                    // (in this case the units clearly do not match)
                    if (!modelClass.isDictionary() && otherModelClass.isDictionary()) {
                        return;
                    }
                    if (!modelClassIsRoot && !otherModelClassIsRoot) {
                        // reference from non-root entity to non-root
                        IntegrityCheckReturnedColumns returnedColumns =
                                new IntegrityCheckReturnedColumns(modelClass.getName(), "object_id",
                                        modelClass.getTableName(), "aggregateroot_id",
                                        "t1.aggregateroot_id", otherModelClass.getTableName(),
                                        "aggregateroot_id", "t2.aggregateroot_id", type.getCheckType(),
                                        type.getErrorCode(), String.format(type.getErrorDetails(), property.getColumnName(), "t1." + property.getColumnName(), otherModelClass.getTableName()));

                        String sql = String.format(type.getSqlQuery(), returnedColumns, modelClass.getTableName(), property.getColumnName(), otherModelClass.getTableName(), "aggregateroot_id", "aggregateroot_id");
                        listSql.add(sql);

                    } else if (modelClassIsRoot && !otherModelClassIsRoot) {
                        // The link from the root entity to the non-root one
                        IntegrityCheckReturnedColumns returnedColumns =
                                new IntegrityCheckReturnedColumns(modelClass.getName(), "object_id",
                                        modelClass.getTableName(), "object_id",
                                        "t1.object_id", otherModelClass.getTableName(),
                                        "aggregateroot_id", "t2.aggregateroot_id", type.getCheckType(),
                                        type.getErrorCode(), String.format(type.getErrorDetails(), property.getColumnName(), "t1." + property.getColumnName(), otherModelClass.getTableName()));

                        String sql = String.format(type.getSqlQuery(), returnedColumns, modelClass.getTableName(), property.getColumnName(), otherModelClass.getTableName(), "aggregateroot_id", "object_id");
                        listSql.add(sql);

                    } else if (!modelClassIsRoot && otherModelClassIsRoot) {
                        // The link from a non-root entity to the root entity
                        IntegrityCheckReturnedColumns returnedColumns =
                                new IntegrityCheckReturnedColumns(modelClass.getName(), "object_id",
                                        modelClass.getTableName(), "aggregateroot_id",
                                        "t1.aggregateroot_id", otherModelClass.getTableName(),
                                        "object_id", "t2.object_id", type.getCheckType(),
                                        type.getErrorCode(), String.format(type.getErrorDetails(), property.getColumnName(), "t1." + property.getColumnName(), otherModelClass.getTableName()));

                        String sql = String.format(type.getSqlQuery(), returnedColumns, modelClass.getTableName(), property.getColumnName(), otherModelClass.getTableName(), "object_id", "aggregateroot_id");
                        listSql.add(sql);
                    }
                }
            }
        });

    }

    /**
     * Checks that all explicitly marked references (integrity-check="true") should refer to existing objects in the database (records - i.e., the reference value = PK of the record)
     *
     * @param type
     * @param xmlModel
     */
    private void generateSqlForCheckReference(DBIntegrityCheckType type, XmlModel xmlModel) {
        List<XmlModelClassProperty> refProperties = new ArrayList<>();
        xmlModel.getClassesAsList().stream()
                .filter(xmlModelClass -> Objects.nonNull(xmlModelClass.getTableName()))
                .filter(xmlModelClass -> !xmlModelClass.isEmbeddable())
                .forEach(xmlModelClass -> xmlModelClass.getPropertiesAsList().stream()
                        .filter(property -> property.getCategory() == PropertyType.REFERENCE
                                && property.isEmbedded()
                                && !property.isParent()
                                && property.isExternalLink()
                                && !property.isExternalSoftReference()
                                && property.isIntegrityCheck()
                                && Objects.isNull(property.getCollectionType()))
                        .forEach(refProperties::add));

        refProperties.forEach(property -> {
            XmlModelClass modelClass = property.getModelClass();
            String originalType = property.getOriginalType();
            if (Objects.nonNull(originalType)) {
                XmlModelClass otherModelClass = modelClass.getModel().getClass(originalType);
                Optional<XmlEmbeddedList> optionalXmlEmbeddedList = modelClass.getEmbeddedPropertyList().stream()
                        .filter(xmlEmbeddedList -> property.getName().equals(xmlEmbeddedList.getName()))
                        .findFirst();
                if (optionalXmlEmbeddedList.isPresent()) {
                    XmlEmbeddedList xmlEmbeddedList = optionalXmlEmbeddedList.get();
                    xmlEmbeddedList.getEmbeddedPropertyList().forEach(embeddedProperty -> {
                        if (ExternalReferenceGenerator.ENTITY_ID.equals(embeddedProperty.getName())) {
                            IntegrityCheckReturnedColumns returnedColumns =
                                    new IntegrityCheckReturnedColumns(modelClass.getName(), "object_id",
                                            modelClass.getTableName(), embeddedProperty.getColumnName(),
                                            String.format("t1.%s", embeddedProperty.getColumnName()), otherModelClass.getTableName(),
                                            "object_id", "''", type.getCheckType(),
                                            type.getErrorCode(), String.format(type.getErrorDetails(), "entities " + otherModelClass.getTableName()));

                            String sql = String.format(type.getSqlQuery(), returnedColumns, modelClass.getTableName(), embeddedProperty.getColumnName(), otherModelClass.getTableName());
                            listSql.add(sql);
                        } else if (ExternalReferenceGenerator.ROOT_ENTITY_ID.equals(embeddedProperty.getName())) {
                            Optional<XmlModelClassProperty> optionalAggRootProperty = otherModelClass.getPropertiesAsList().stream().filter(prop -> AGGREGATE_ROOT.equals(prop.getName())).findFirst();
                            if (optionalAggRootProperty.isPresent()) {
                                XmlModelClassProperty aggRootProperty = optionalAggRootProperty.get();
                                XmlModelClass rootOtherModelClass = xmlModel.getClass(aggRootProperty.getType());
                                if (Objects.nonNull(rootOtherModelClass.getTableName())) {
                                    IntegrityCheckReturnedColumns returnedColumns =
                                            new IntegrityCheckReturnedColumns(modelClass.getName(), "object_id",
                                                    modelClass.getTableName(), embeddedProperty.getColumnName(),
                                                    String.format("t1.%s", embeddedProperty.getColumnName()), rootOtherModelClass.getTableName(),
                                                    "object_id", "''", type.getCheckType(),
                                                    type.getErrorCode(), String.format(type.getErrorDetails(), "root of aggregate " + rootOtherModelClass.getTableName()));

                                    String sql = String.format(type.getSqlQuery(), returnedColumns, modelClass.getTableName(), embeddedProperty.getColumnName(), rootOtherModelClass.getTableName());
                                    listSql.add(sql);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Check that for reference links to child entities (also integrity-check = "true"): the entity aggregate according to the link should match the aggregate specified in the link,
     * check for match of aggregate root ID in reference and ID of aggregate root specified in child entity by link
     *
     * @param type
     * @param xmlModel
     */
    private void generateSqlForCheckReferenceToChildAgg(DBIntegrityCheckType type, XmlModel xmlModel) {
        class EmbeddedColumnNames {
            String entityColumnName;
            String rootColumnName;

            public boolean columnsIsFilled() {
                return Objects.nonNull(entityColumnName) && Objects.nonNull(rootColumnName);
            }
        }
        Map<XmlModelClassProperty, EmbeddedColumnNames> mapRefToChildProperties = new HashMap<>();

        xmlModel.getClassesAsList().stream()
                .filter(xmlModelClass -> Objects.nonNull(xmlModelClass.getTableName()))
                .filter(xmlModelClass -> !xmlModelClass.isEmbeddable())
                .forEach(xmlModelClass -> xmlModelClass.getPropertiesAsList().stream()
                        .filter(property -> property.getCategory() == PropertyType.REFERENCE
                                && property.isEmbedded()
                                && !property.isParent()
                                && property.isExternalLink()
                                && !property.isExternalSoftReference()
                                && property.isIntegrityCheck()
                                && Objects.isNull(property.getCollectionType()))
                        .forEach(property -> {
                                    XmlModelClass modelClass = property.getModelClass();
                                    Optional<XmlEmbeddedList> optionalXmlEmbeddedList = modelClass.getEmbeddedPropertyList().stream()
                                            .filter(xmlEmbeddedList -> property.getName().equals(xmlEmbeddedList.getName()))
                                            .findFirst();
                                    optionalXmlEmbeddedList.ifPresent(xmlEmbeddedList -> {
                                        if (xmlEmbeddedList.getEmbeddedPropertyList().size() == 2) {
                                            EmbeddedColumnNames embeddedColumnNames = new EmbeddedColumnNames();
                                            xmlEmbeddedList.getEmbeddedPropertyList().forEach(embeddedProperty -> {
                                                if (ExternalReferenceGenerator.ENTITY_ID.equals(embeddedProperty.getName())) {
                                                    embeddedColumnNames.entityColumnName = embeddedProperty.getColumnName();
                                                } else if (ExternalReferenceGenerator.ROOT_ENTITY_ID.equals(embeddedProperty.getName())) {
                                                    embeddedColumnNames.rootColumnName = embeddedProperty.getColumnName();
                                                }
                                            });
                                            if (embeddedColumnNames.columnsIsFilled()) {
                                                mapRefToChildProperties.put(property, embeddedColumnNames);
                                            }
                                        }
                                    });
                                }
                        ));

        mapRefToChildProperties.keySet().forEach(property -> {
            XmlModelClass modelClass = property.getModelClass();
            String originalType = property.getOriginalType();
            if (Objects.nonNull(originalType)) {
                XmlModelClass otherModelClass = modelClass.getModel().getClass(originalType);
                Optional<XmlModelClassProperty> optionalAggRootProperty = otherModelClass.getPropertiesAsList().stream().filter(prop -> AGGREGATE_ROOT.equals(prop.getName())).findFirst();
                if (optionalAggRootProperty.isPresent()) {
                    XmlModelClassProperty aggRootProperty = optionalAggRootProperty.get();
                    XmlModelClass rootOtherModelClass = xmlModel.getClass(aggRootProperty.getType());
                    if (Objects.nonNull(rootOtherModelClass.getTableName())) {
                        EmbeddedColumnNames embeddedColumnNames = mapRefToChildProperties.get(property);
                        IntegrityCheckReturnedColumns returnedColumns =
                                new IntegrityCheckReturnedColumns(modelClass.getName(), "object_id",
                                        modelClass.getTableName(), embeddedColumnNames.entityColumnName,
                                        String.format("t1.%s", embeddedColumnNames.entityColumnName), otherModelClass.getTableName(),
                                        "aggregateroot_id", "t3.aggregateroot_id", type.getCheckType(),
                                        type.getErrorCode(), type.getErrorDetails());

                        String sql = String.format(type.getSqlQuery(), returnedColumns, modelClass.getTableName(), embeddedColumnNames.rootColumnName,
                                rootOtherModelClass.getTableName(), otherModelClass.getTableName(), embeddedColumnNames.entityColumnName);
                        listSql.add(sql);
                    }
                }
            }
        });
    }

    /**
     * Checking the completion of required fields (mandatory="true")
     *
     * @param type
     * @param xmlModel
     */
    private void generateSqlForCheckMandatory(DBIntegrityCheckType type, XmlModel xmlModel) {
        List<XmlModelClassProperty> mandatoryProperties = new ArrayList<>();
        xmlModel.getClassesAsList().stream()
                .filter(xmlModelClass -> Objects.nonNull(xmlModelClass.getTableName()))
                .forEach(xmlModelClass -> xmlModelClass.getPropertiesAsList().stream()
                        .filter(UserXmlModelClassProperty::isMandatory)
                        .filter(property -> Objects.nonNull(property.getColumnName()))
                        .forEach(mandatoryProperties::add));

        mandatoryProperties.forEach(property -> {
            XmlModelClass xmlModelClass = property.getModelClass();
            IntegrityCheckReturnedColumns returnedColumns =
                    new IntegrityCheckReturnedColumns(xmlModelClass.getName(), "object_id",
                            xmlModelClass.getTableName(), property.getColumnName(),
                            String.format("cast(t1.%s as varchar(254))", property.getColumnName()), "",
                            "", "''", type.getCheckType(),
                            type.getErrorCode(), type.getErrorDetails());

            String sql = String.format(type.getSqlQuery(), returnedColumns, xmlModelClass.getTableName(), property.getColumnName());
            listSql.add(sql);

        });
    }

    /**
     * Checking that SYS_OWNERID is filled for root aggregates tables
     *
     * @param type
     * @param xmlModel
     */
    private void generateSqlForCheckAggregateRootOwnerId(DBIntegrityCheckType type, XmlModel xmlModel) {
        List<XmlModelClass> aggregateRootClasses = xmlModel.getClassesAsList().stream()
                .filter(xmlModelClass -> !xmlModelClass.isEmbeddable() && !xmlModelClass.isDictionary())
                .filter(getAggregateRootClassPredicate())
                .collect(Collectors.toList());

        aggregateRootClasses.stream()
                .filter(xmlModelClass -> Objects.nonNull(xmlModelClass.getTableName()))
                .filter(xmlModelClass -> xmlModelClass.getPropertiesWithAllIncome().stream()
                        .anyMatch(xmlModelClassProperty -> SYS_OWNERID.equalsIgnoreCase(xmlModelClassProperty.getColumnName())))
                .forEach(xmlModelClass -> {

                    IntegrityCheckReturnedColumns returnedColumns =
                            new IntegrityCheckReturnedColumns(xmlModelClass.getName(), "object_id",
                                    xmlModelClass.getTableName(), SYS_OWNERID,
                                    String.format("t1.%s", SYS_OWNERID), "",
                                    "", "''", type.getCheckType(),
                                    type.getErrorCode(), type.getErrorDetails());

                    String sql = String.format(type.getSqlQuery(), returnedColumns, xmlModelClass.getTableName());
                    listSql.add(sql);
                });
    }

    private Predicate<XmlModelClass> getAggregateRootClassPredicate() {
        return xmlModelClass -> xmlModelClass.getName().equals(xmlModelClass.getRootType());
    }

}
