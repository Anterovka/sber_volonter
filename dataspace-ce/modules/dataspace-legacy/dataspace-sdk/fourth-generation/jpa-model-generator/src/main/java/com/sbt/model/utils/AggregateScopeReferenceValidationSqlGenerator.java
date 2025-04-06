package com.sbt.model.utils;

import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.parameters.enums.Changeable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AggregateScopeReferenceValidationSqlGenerator {

    public static class Result {
        private final String className;
        private final String propertyName;
        private final String sql;

        public Result(String className, String propertyName, String sql) {
            this.className = className;
            this.propertyName = propertyName;
            this.sql = sql;
        }

        public String getClassName() {
            return className;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public String getSql() {
            return sql;
        }

        @Override
        public String toString() {

            return String.format(
                    "-- %s.%s%n%s;%n",
                    this.getClassName(),
                    this.getPropertyName(),
                    this.getSql()
            );

        }
    }

    public List<Result> generate(XmlModel xmlModel) {

        final List<Result> results = new ArrayList<>();

        final SqlGenerator sqlGenerator = new SqlGenerator();

        final ModelDescription modelDescription = new ModelDescription(Objects.requireNonNull(xmlModel));

        modelDescription
                .stream()
                .sorted(Comparator.comparing(ModelDescription.ClassDescription::getName))
                .forEach(classDescription -> {

                    final ModelDescription.ClassDescription baseClass = modelDescription.baseClassOf(classDescription.getName());

                    classDescription
                            .getReferences()
                            .stream()
                            .sorted(Comparator.comparing(XmlModelClassProperty::getName))
                            .forEach(property -> {

                                final ModelDescription.ClassDescription propertyBaseClass = modelDescription.baseClassOf(property.getType());

                                final String sql = sqlGenerator.generateSql(
                                        classDescription.getTableName(),
                                        property.getColumnName(),
                                        baseClass.toTableDescription(),
                                        propertyBaseClass.toTableDescription()
                                );

                                results.add(new Result(
                                        classDescription.getName(),
                                        property.getName(),
                                        sql
                                ));

                            });

                });

        return results;

    }

    public static class SqlGenerator {

        public static class TableDescription {

            private final String name;
            private final String aggregateIdFieldName;

            public TableDescription(String name, boolean containsAggregateRootField) {
                this.name = name;
                this.aggregateIdFieldName = containsAggregateRootField ? "AGGREGATEROOT_ID" : "OBJECT_ID";
            }

            public String getName() {
                return name;
            }

            public String getAggregateIdFieldName() {
                return aggregateIdFieldName;
            }

        }

        public String generateSql(
                String tableName,
                String columnName,
                TableDescription baseTableDescription,
                TableDescription referenceTableDescription
        ) {

            final String baseTableAlias;
            final String baseTableJoin;

            if (baseTableDescription.getName().equals(tableName)) {
                baseTableAlias = "fo";
                baseTableJoin = "";
            } else {
                baseTableAlias = "bo";
                baseTableJoin = " JOIN " +
                        baseTableDescription.getName() +
                        " bo ON bo.OBJECT_ID = fo.OBJECT_ID";
            }

            return "SELECT " +
                    "fo.OBJECT_ID, " +
                    baseTableAlias + ".TYPE, " +
                    "fo." + columnName +
                    " FROM " + tableName + " fo" +
                    baseTableJoin +
                    " JOIN " +
                    referenceTableDescription.getName() +
                    " fb ON fb.OBJECT_ID = fo." + columnName +
                    " WHERE " +
                    "fo." + columnName + " IS NOT NULL AND " +
                    "fb." + referenceTableDescription.getAggregateIdFieldName() +
                    " <> " +
                    baseTableAlias + "." + baseTableDescription.getAggregateIdFieldName() +
                    " ORDER BY fo.OBJECT_ID";

        }

    }

    public static class ModelDescription {

        public static class ClassDescription {
            private final XmlModelClass modelClass;
            private final boolean aggregateElement;
            private final List<XmlModelClassProperty> references = new ArrayList<>();

            public ClassDescription(XmlModelClass modelClass,
                                    boolean aggregateElement,
                                    List<XmlModelClassProperty> references) {
                this.modelClass = Objects.requireNonNull(modelClass);
                this.aggregateElement = aggregateElement;
                this.references.addAll(Optional.ofNullable(references).orElse(Collections.emptyList()));
            }

            public String getName() {
                return modelClass.getName();
            }

            public String getTableName() {
                return modelClass.getTableName();
            }

            public boolean isAggregateElement() {
                return aggregateElement;
            }

            public List<XmlModelClassProperty> getReferences() {
                return references;
            }

            public SqlGenerator.TableDescription toTableDescription() {

                return new SqlGenerator.TableDescription(
                        getTableName(),
                        isAggregateElement()
                );

            }

        }

        private final XmlModel model;
        private final Map<String, ClassDescription> classDescriptionMap;

        public ModelDescription(XmlModel model) {

            this.model = Objects.requireNonNull(model);

            classDescriptionMap = model
                    .getClassesAsList()
                    .stream()
                    .filter(xmlModelClass -> Objects.nonNull(xmlModelClass.getTableName()))
                    .filter(this::isUserClassLike)
                    .filter(xmlModelClass -> xmlModelClass.getClassAccess() == Changeable.UPDATE)
                    .map(this::classDescription)
                    .collect(Collectors.toMap(
                                    ClassDescription::getName,
                                    Function.identity()
                            )
                    );

        }

        public ClassDescription baseClassOf(String name) {

            final XmlModelClass aClass = Objects.requireNonNull(
                    model.getClassNullable(name),
                    "Class '" + name + "' not found..."
            );

            XmlModelClass baseClass = aClass;
            XmlModelClass current = aClass;

            while (Objects.nonNull(current)) {

                if (not(current.isAbstract())) {
                    baseClass = current;
                }

                current = current.getExtendedClass();

            }

            return Objects.requireNonNull(
                    classDescriptionMap.get(baseClass.getName()),
                    "Class description for '" + baseClass.getName() + "' not found..."
            );

        }

        public Stream<ClassDescription> stream() {
            return classDescriptionMap
                    .values()
                    .stream()
                    .filter(classDescription -> not(classDescription.getReferences().isEmpty()));
        }

        private ClassDescription classDescription(XmlModelClass xmlModelClass) {

            final Map<String, XmlModelClassProperty> propertyMap = new HashMap<>();
            XmlModelClass current = xmlModelClass;

            do {

                propertyMap.putAll(
                        current
                                .getPropertiesAsList()
                                .stream()
                                .collect(
                                        Collectors
                                                .toMap(
                                                        XmlModelClassProperty::getName,
                                                        Function.identity()
                                                )
                                )
                );

                current = current.getExtendedClass();

            } while (Objects.nonNull(current) && nullAsFalse(current.isAbstract()));

            return new ClassDescription(
                    xmlModelClass,
                    propertyMap.containsKey(JpaConstants.AGGREGATE_ROOT),
                    propertyMap
                            .values()
                            .stream()
                            .filter(this::isReferenceProperty)
                            .collect(Collectors.toList())
            );

        }

        private boolean nullAsFalse(Boolean value) {
            return Objects.nonNull(value) && value;
        }

        private boolean not(Boolean value) {
            return !nullAsFalse(value);
        }

        private boolean isUserClassLike(XmlModelClass xmlModelClass) {

            return not(xmlModelClass.isDictionary()) && not(xmlModelClass.isEmbeddable());

        }

        private boolean isReferenceProperty(XmlModelClassProperty property) {

            final XmlModelClass propertyClass = model.getClassNullable(property.getType());

            return Optional.ofNullable(propertyClass).filter(this::isUserClassLike).isPresent()
                    && Objects.isNull(property.getCollectionType())
                    && Objects.isNull(property.getMappedBy())
                    && not(property.isParent())
                    && not(property.isExternalLink())
                    && not(property.isExternalSoftReference())
                    && not(JpaConstants.AGGREGATE_ROOT.equalsIgnoreCase(property.getName()));

        }

    }

}
