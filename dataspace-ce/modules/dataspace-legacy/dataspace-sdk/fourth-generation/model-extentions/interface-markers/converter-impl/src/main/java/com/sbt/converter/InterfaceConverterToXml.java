package com.sbt.converter;

import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.ClassStrategy;
import com.sbt.mg.data.model.CollectionType;
import com.sbt.mg.data.model.XmlEnumValue;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassEnum;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.id.XmlId;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.parameters.enums.Changeable;
import com.sbt.parameters.enums.IdCategory;
import com.sbt.xmlmarker.Abstract;
import com.sbt.xmlmarker.Access;
import com.sbt.xmlmarker.Aggregate;
import com.sbt.xmlmarker.CollectionTable;
import com.sbt.xmlmarker.CompositeId;
import com.sbt.xmlmarker.DefaultValue;
import com.sbt.xmlmarker.Dictionary;
import com.sbt.xmlmarker.Embeddable;
import com.sbt.xmlmarker.Extends;
import com.sbt.xmlmarker.Final;
import com.sbt.xmlmarker.Id;
import com.sbt.xmlmarker.IdField;
import com.sbt.xmlmarker.Ignore;
import com.sbt.xmlmarker.Indexes;
import com.sbt.xmlmarker.Interfaces;
import com.sbt.xmlmarker.Label;
import com.sbt.xmlmarker.Length;
import com.sbt.xmlmarker.Lockable;
import com.sbt.xmlmarker.Mandatory;
import com.sbt.xmlmarker.MappedBy;
import com.sbt.xmlmarker.Name;
import com.sbt.xmlmarker.NoAffinity;
import com.sbt.xmlmarker.NoBaseEntityParent;
import com.sbt.xmlmarker.NoIdempotence;
import com.sbt.xmlmarker.NoJPA;
import com.sbt.xmlmarker.SingleClass;
import com.sbt.xmlmarker.SystemClass;
import com.sbt.xmlmarker.Table;
import com.sbt.xmlmarker.Temporal;
import com.sbt.xmlmarker.Type;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class InterfaceConverterToXml {

    private static final String SYS_PREFIX = "Sys";
    private static final String PK_SUFFIX_PREFIX = "PK";

    private InterfaceConverterToXml() {

    }

    public static XmlModelClass convertToXml(Class<?> interfaceMarkedClass, XmlModel model) {

        return convertToXml(interfaceMarkedClass, model, aClass -> {});
    }

    public static XmlModelClass convertToXml(Class<?> interfaceMarkedClass,
                                             XmlModel model,
                                             Consumer<Class<?>> fieldsLogic) {
        if (interfaceMarkedClass.isEnum()) {
            XmlModelClassEnum xmlClassEnum = new XmlModelClassEnum();
            xmlClassEnum.setDeprecated(false);

            Name nameAnnotation = getAnnotation(Name.class, interfaceMarkedClass);
            if (nameAnnotation != null) {
                xmlClassEnum.setName(nameAnnotation.value());
            } else {
                xmlClassEnum.setName(addSysPrefixToName(interfaceMarkedClass.getSimpleName()));
            }

            Label label = getAnnotation(Label.class, interfaceMarkedClass);
            if (label != null) {
                xmlClassEnum.setLabel(label.value());
            } else {
                xmlClassEnum.setLabel("no label");
            }

            Access access = getAnnotation(Access.class, interfaceMarkedClass);
            if (access != null) {
                xmlClassEnum.setChangeable(access.value());
            } // Otherwise by default UPDATE

            List<XmlEnumValue> enumConstants = Arrays.stream(interfaceMarkedClass.getEnumConstants())
                    .map(it -> new XmlEnumValue(it.toString(), "", ""))
                    .collect(Collectors.toList());
            xmlClassEnum.getEnumValues().addAll(enumConstants);


            if (model.getEnums().stream().noneMatch(it -> it.getName().equals(xmlClassEnum.getName()))) {
                model.addEnum(xmlClassEnum);
            }


            return null;

        } else {
            XmlModelClass modelClass = new XmlModelClass();
            modelClass.setServiceClass(true);

            final SystemClass systemClassAnno = getAnnotation(SystemClass.class, interfaceMarkedClass);

            final Label labelAnno = getAnnotation(Label.class, interfaceMarkedClass);
            if (labelAnno != null) {
                modelClass.setLabel(labelAnno.value());
            }

            Name nameAnnotation = getAnnotation(Name.class, interfaceMarkedClass);
            modelClass.setName(nameAnnotation != null
                    ? nameAnnotation.value() : addSysPrefixToName(interfaceMarkedClass.getSimpleName()));

            Access accessAnnotation = getAnnotation(Access.class, interfaceMarkedClass);
            if (systemClassAnno != null) {
                modelClass.setClassAccess(Changeable.SYSTEM);
            }
            if (accessAnnotation != null) {
                modelClass.setClassAccess(accessAnnotation.value());
            }

            if (getAnnotation(Final.class, interfaceMarkedClass) != null) {
                modelClass.setFinalClass(Boolean.TRUE);
            }

            if (getAnnotation(NoAffinity.class, interfaceMarkedClass) != null) {
                modelClass.setUseAffinity(false);
            }

            if (getAnnotation(Abstract.class, interfaceMarkedClass) != null) {
                modelClass.setAbstract();
            }

            if (getAnnotation(Embeddable.class, interfaceMarkedClass) != null) {
                modelClass.setEmbeddable(Boolean.TRUE);
            }

            if (getAnnotation(Dictionary.class, interfaceMarkedClass) != null) {
                modelClass.setDictionary(true);
            }

            if (getAnnotation(NoJPA.class, interfaceMarkedClass) != null || systemClassAnno != null) {
                modelClass.setJPA(Boolean.FALSE);
                modelClass.setNoIdempotence(true);
            }

            if (getAnnotation(Lockable.class, interfaceMarkedClass) != null) {
                modelClass.setLockable(Boolean.TRUE);
            }

            if (getAnnotation(NoBaseEntityParent.class, interfaceMarkedClass) != null) {
                modelClass.setNoBaseEntity(true);
            }

            if (getAnnotation(NoIdempotence.class, interfaceMarkedClass) != null) {
                modelClass.setNoIdempotence(true);
            }

            Id idCategory = getAnnotation(Id.class, interfaceMarkedClass);
            if (idCategory != null) {
                //If the type of id is String, then its length can be changed
                if (String.class.getSimpleName().equals(idCategory.type())) {
                    modelClass.setId(new XmlId(idCategory.type(), idCategory.value(), idCategory.length()));
                } else {
                    modelClass.setId(new XmlId(idCategory.type(), idCategory.value()));
                }
            }

            Interfaces interfaces = getAnnotation(Interfaces.class, interfaceMarkedClass);
            if (interfaces != null) {
                Arrays.asList(interfaces.values()).forEach(modelClass.getSystemInterfaces()::add);
            }

            Extends anExtends = getAnnotation(Extends.class, interfaceMarkedClass);
            if (anExtends != null) {
                modelClass.setExtendedClassName(anExtends.value());
            }

            //Because classes and collection fields are assigned physical names, it is necessary to bind them to the model.
            // until they are fully formed. Otherwise, the check for duplicate physical names when assigning will result in an error.
            model.addClassWithoutCheck(modelClass);

            getMethods(interfaceMarkedClass).stream()
                    .filter(method -> !method.getName().startsWith("set"))
                    .filter(method -> method.getAnnotation(Ignore.class) == null)
                    .sorted(Comparator.comparing(Method::getName))
                    .forEach(method -> {
                        XmlModelClassProperty property = new XmlModelClassProperty();
                        modelClass.addProperty(property);
                        handleMethod(method, property);
                    });

            initIndexes(modelClass, interfaceMarkedClass);

            if (!modelClass.isAbstract() && !modelClass.isEmbeddable()) {
                modelClass.setBaseClassMark(true)
                        .setStrategy(ClassStrategy.JOINED);
            }

            if (!modelClass.isAbstract() && !modelClass.isEmbeddable()) {
                modelClass.setBaseClassMark(true)
                        .setStrategy(ClassStrategy.JOINED);
            }

            Table table = getAnnotation(Table.class, interfaceMarkedClass);
            if (table != null && StringUtils.isNotEmpty(table.value())) {
                modelClass.setTableName(table.value());
            }

            initCompositeId(interfaceMarkedClass, modelClass);

            return modelClass;

        }
    }

    private static String addSysPrefixToName(String name) {
        return SYS_PREFIX + name;
    }

    private static String addPKSuffixToName(String name) {
        return name + PK_SUFFIX_PREFIX;
    }

    private static void initCompositeId(Class<?> interfaceMarkedClass, XmlModelClass modelClass) {
        final CompositeId annotation = getAnnotation(CompositeId.class, interfaceMarkedClass);
        if (annotation == null) {
            return;
        }

        final String[] fields = annotation.fields();
        if (fields.length == 0) {
            throw new IllegalStateException("For composite index, it is necessary to set the fields.");
        }

        final List<String> absentProps = Arrays.stream(fields)
                .filter(it -> !modelClass.containsProperty(it))
                .collect(Collectors.toList());
        if (!absentProps.isEmpty()) {
            throw new IllegalStateException("Fields not found: " + absentProps);
        }

        final String embeddedClassName = addPKSuffixToName(modelClass.getName());
        final XmlModelClass embeddedClass = XmlModelClass.Builder
                .create()
                .setName(embeddedClassName)
                .setEmbeddable(true)
                .setId(new XmlId("String", IdCategory.NO_ID))
                .setLabel("PrimaryKey for " + modelClass.getName())
                .setClassAccess(Changeable.SYSTEM)
                .setNoJpa()
                .setNoIdempotence()
                .build();

        Arrays.stream(fields)
                .forEach(propertyName -> {
                    final XmlModelClassProperty property = modelClass.getProperty(propertyName);
                    embeddedClass.addProperty(property);
                    modelClass.removeProperty(propertyName);
                });

        modelClass.getModel().addClass(embeddedClass);

        modelClass.setId(new XmlId(embeddedClassName, IdCategory.MANUAL));
        modelClass.addProperty(XmlModelClassProperty.Builder
                .create()
                .setName(JpaConstants.COMPOSITE_ID)
                .setId(true)
                .setMandatory(true)
                .setChangeable(Changeable.READ_ONLY)
                .setType(embeddedClassName)
                .build());
    }

    private static void handleMethod(Method method, XmlModelClassProperty property) {
        property.setName(readFieldName(method));
        Label label = method.getAnnotation(Label.class);
        if (label != null) {
            property.setLabel(label.value());
        }

        Length length = method.getAnnotation(Length.class);
        if (length != null) {
            property.setLength(length.value());
        }

        if (method.getAnnotation(Mandatory.class) != null) {
            property.setMandatory(Boolean.TRUE);
        }

        Access accessAnnotation = method.getAnnotation(Access.class);
        if (accessAnnotation != null) {
            property.setAccess(accessAnnotation.access());
            property.setChangeable(accessAnnotation.value());
        }

        DefaultValue defaultValue = method.getAnnotation(DefaultValue.class);
        if (defaultValue != null) {
            property.setDefaultValue(defaultValue.value());
        }

        CollectionTable collectionTable = method.getAnnotation(CollectionTable.class);
        if (collectionTable != null && StringUtils.isNotEmpty(collectionTable.value())) {
            property.setCollectionTableName(collectionTable.value());
        }

        if (method.getAnnotation(Aggregate.class) != null) {
            property.setParent();
        }

        MappedBy mappedBy = method.getAnnotation(MappedBy.class);
        if (mappedBy != null) {
            property.setMappedBy(mappedBy.value());
        }

        Temporal temporal = method.getAnnotation(Temporal.class);
        if (temporal != null) {
            property.setTemporalType(temporal.value());
        }

        IdField idField = method.getAnnotation(IdField.class);
        if (idField != null) {
            property.setId(true);
            property.setMandatory(true);
        }

        setType(property, method);
    }

    private static String readFieldName(Method method) {
        return StringUtils.uncapitalize(method.getName().substring(3));
    }

    //refactoring: PropertyHelperBuilder to XmlModelClassProperty
    private static void setType(XmlModelClassProperty property, Method method) {
        Class<?> returnType = method.getReturnType();
        if (List.class.isAssignableFrom(returnType)) {
            setCollection(property, method, CollectionType.LIST);
        } else if (Set.class.isAssignableFrom(returnType)) {
            setCollection(property, method, CollectionType.SET);
        } else {
            if (method.isAnnotationPresent(Type.class)) {
                property.setType(method.getAnnotation(Type.class).value());
            } else {
                property.setType(returnType.getSimpleName());
            }
        }
    }

    //refactoring: PropertyHelperBuilder to XmlModelClassProperty
    private static void setCollection(XmlModelClassProperty property, Method method, CollectionType collectionType) {
        property.setCollectionType(collectionType);
        property.setType(getGenericType(method).getSimpleName());
    }

    private static Class<?> getGenericType(Method method) {
        java.lang.reflect.Type genericReturnType = method.getGenericReturnType();
        if (genericReturnType instanceof ParameterizedType) {
            java.lang.reflect.Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
            if (actualTypeArguments.length == 1) {
                return (Class<?>) actualTypeArguments[0];
            }
        }
        throw new UnsupportedOperationException("Can't read generic type of method=" + method.toString());
    }

    private static void initIndexes(XmlModelClass modelClass, Class<?> entityClass) {
        Indexes indexes = getAnnotation(Indexes.class, entityClass);

        if (indexes == null) {
            return;
        }

        Arrays.stream(indexes.value()).forEach(index -> {
            List<XmlModelClassProperty> properties = Arrays.stream(index.properties())
                    .map(s -> modelClass.getPropertyWithHierarchyInSingleTable(s)).collect(Collectors.toList());

            ModelHelper.addComplexIndexToClass(modelClass, index.unique(), index.primary(), properties, false);
        });
    }

    private static <A extends Annotation> A getAnnotation(Class<A> annotation, Class<?>... classes) {
        for (Class<?> aClass : classes) {
            final A anno = aClass.getAnnotation(annotation);
            if (!Objects.isNull(anno)) {
                return anno;
            }
        }
        Set<Class<?>> currentClasses = new HashSet<>();

        Arrays.stream(classes)
                .forEach(it -> {
                    if (!Objects.isNull(it.getSuperclass())) {
                        currentClasses.add(it.getSuperclass());
                    }

                    currentClasses.addAll(Arrays.stream(it.getInterfaces())
                            .collect(Collectors.toSet()));
                });

        if (currentClasses.isEmpty()) {
            return null;
        }

        return getAnnotation(annotation, currentClasses.toArray(new Class<?>[]{}));
    }

    private static List<Method> getMethods(Class<?> interfaceMarkedClass) {
        List<Method> result = new ArrayList<>(Arrays.asList(interfaceMarkedClass.getDeclaredMethods()));

        Arrays.stream(interfaceMarkedClass.getInterfaces())
                .filter(it -> !Objects.isNull(it.getAnnotation(SingleClass.class)))
                .forEach(it -> result.addAll(getMethods(it)));

        return result;
    }
}
