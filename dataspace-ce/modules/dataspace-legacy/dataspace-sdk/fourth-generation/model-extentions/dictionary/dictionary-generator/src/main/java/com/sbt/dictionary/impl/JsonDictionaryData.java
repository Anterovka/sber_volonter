package com.sbt.dictionary.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbt.dictionary.CheckParams;
import com.sbt.dictionary.api.DictionaryData;
import com.sbt.dictionary.exceptions.DictionaryConsistencyException;
import com.sbt.dictionary.exceptions.DictionaryDeleteException;
import com.sbt.dictionary.exceptions.DictionaryEntityNotFoundException;
import com.sbt.dictionary.exceptions.DuplicateDictionaryIdException;
import com.sbt.dictionary.exceptions.MandatoryFieldEmptyException;
import com.sbt.dictionary.exceptions.NotDictionaryReferenceException;
import com.sbt.dictionary.exceptions.ObjectWithNoIdException;
import com.sbt.dictionary.exceptions.ParseException;
import com.sbt.dictionary.exceptions.UniqueConstraintException;
import com.sbt.dictionary.exceptions.WrongFieldException;
import com.sbt.dictionary.impl.typecheckers.BigDecimalChecker;
import com.sbt.dictionary.impl.typecheckers.BooleanChecker;
import com.sbt.dictionary.impl.typecheckers.ByteChecker;
import com.sbt.dictionary.impl.typecheckers.CharacterChecker;
import com.sbt.dictionary.impl.typecheckers.CollectionChecker;
import com.sbt.dictionary.impl.typecheckers.DateChecker;
import com.sbt.dictionary.impl.typecheckers.DoubleChecker;
import com.sbt.dictionary.impl.typecheckers.EmptyChecker;
import com.sbt.dictionary.impl.typecheckers.EmptyValueChecker;
import com.sbt.dictionary.impl.typecheckers.EnumChecker;
import com.sbt.dictionary.impl.typecheckers.FloatChecker;
import com.sbt.dictionary.impl.typecheckers.IntegerChecker;
import com.sbt.dictionary.impl.typecheckers.LocalDateChecker;
import com.sbt.dictionary.impl.typecheckers.LocalDateTimeChecker;
import com.sbt.dictionary.impl.typecheckers.LongChecker;
import com.sbt.dictionary.impl.typecheckers.ShortChecker;
import com.sbt.dictionary.impl.typecheckers.StringChecker;
import com.sbt.dictionary.impl.typecheckers.TypeChecker;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.PropertyNotFoundInClassException;
import com.sbt.mg.exception.common.ExecuteException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sbt.mg.ModelHelper.isPrimitiveType;
import static com.sbt.mg.Helper.wrap;

/**
 * JSON files should be located next to the model in the dictionary/JSON folder
 * Structuring can be done in any way.
 */
public class JsonDictionaryData implements DictionaryData {

    private static final String JSON_EXTENSION = ".json";
    private static final String ID_FIELD = "id";
    private final ObjectMapper mapper = new ObjectMapper();

    private final CheckParams checkParams;

    private Set<File> newDictionaryPlaces;
    private File oldDictionaryPlace;
    private Collection<InputStream> newStreams;
    private Collection<InputStream> currentStreams;
    private Map<String, List<Map<String, Object>>> newData;
    private Map<String, List<Map<String, Object>>> oldData;

    public JsonDictionaryData(CheckParams checkParams) {
        this.checkParams = checkParams;
    }

    @Override
    public void setDictionaryPlace(Set<File> newPlaces, File oldPlace) {
        this.newDictionaryPlaces = newPlaces;
        this.oldDictionaryPlace = oldPlace;
    }

    @Override
    public void setDictionaryStreams(Collection<InputStream> newStreams, Collection<InputStream> currentStreams) {
        this.newStreams = new ArrayList<>();
        if (newStreams != null) {
            this.newStreams.addAll(newStreams);
        }
        this.currentStreams = new ArrayList<>();
        if (currentStreams != null) {
            this.currentStreams.addAll(currentStreams);
        }
    }

    @Override
    public void setDictionaryStreams(Collection<InputStream> newStreams) {
        this.newStreams = new ArrayList<>();
        if (newStreams != null) {
            this.newStreams.addAll(newStreams);
        }
        this.currentStreams = Collections.emptyList();
    }

    @Override
    public void init() {
        if (Objects.isNull(newStreams)) {
            this.newData = readDataFromFiles(newDictionaryPlaces);
            this.oldData = readData(oldDictionaryPlace);
        } else {
            this.newData = readData(newStreams);
            this.oldData = readData(currentStreams);
        }
    }

    @Override
    public void check(XmlModel model) {
        newData.keySet().forEach(entity -> {
            checkClassExistsInModel(model, entity);
            checkAllFieldsExistsInClass(model, entity);
            checkAllEntitiesHaveId(entity);
            checkUniqueIds(entity);
            checkMandatoryFields(model, entity);
            checkUniqueFields(model, entity);
            if (checkParams.isEnableDictionaryDataCheck()) {
                checkDataTypes(model, entity);
            }
        });

        newData.keySet().forEach(entity -> checkConsistency(model, entity));

        checkNoDeleteObjects();
    }

    private void checkClassExistsInModel(XmlModel model, String entity) {
        if (!model.containsClass(entity) || !model.getClass(entity).isDictionary()) {
            throw new DictionaryEntityNotFoundException(entity);
        }
    }

    private void checkAllFieldsExistsInClass(XmlModel model, String entity) {
        XmlModelClass xmlModelClass = model.getClassNullable(entity);
        Collection<String> allInheritedProperties = ModelHelper.getAllPropertiesWithInherited(xmlModelClass).stream()
                .filter(it -> Objects.isNull(it.getMappedBy()))
                .map(XmlModelClassProperty::getName)
                .collect(Collectors.toList());
        Set<String> notFoundFields = newData.get(entity).stream()
                .map(Map::keySet)
                .flatMap(Collection::stream)
                .filter(key -> !ID_FIELD.equals(key))
                .filter(key -> !allInheritedProperties.contains(key))
                .collect(Collectors.toSet());
        if (!notFoundFields.isEmpty()) {
            throw new WrongFieldException(entity, notFoundFields);
        }
    }

    private void checkAllEntitiesHaveId(String entity) {
        if (newData.get(entity).stream()
                .anyMatch(map -> map.get(ID_FIELD) == null)) {
            throw new ObjectWithNoIdException(entity);
        }
    }

    private void checkMandatoryFields(XmlModel model, String entity) {
        XmlModelClass xmlModelClass = model.getClass(entity);
        xmlModelClass.getPropertiesAsList().stream()
                .filter(XmlModelClassProperty::isMandatory)
                .filter(property -> !"type".equals(property.getName()))
                .forEach(property ->
                        newData.get(entity)
                                .forEach(entry -> {
                                    if (entry.get(property.getName()) == null) {
                                        throw new MandatoryFieldEmptyException(entity, property.getName());
                                    }
                                }));
    }

    private void checkUniqueIds(String entity) {
        List<Map<String, Object>> entityObjects = newData.get(entity);
        Set<String> ids = new HashSet<>(entityObjects.size());
        entityObjects.forEach(checkedEntity -> {
            String id = checkedEntity.get(ID_FIELD).toString();
            if (ids.contains(id)) {
                throw new DuplicateDictionaryIdException(entity, id);
            }
            ids.add(id);
        });
    }

    private void checkUniqueFields(XmlModel model, String entity) {
        XmlModelClass xmlModelClass = model.getClass(entity);
        xmlModelClass.getPropertiesAsList().stream()
                .filter(XmlModelClassProperty::isUnique)
                .forEach(property -> {
                    Set<String> uniqueFieldValues = new HashSet<>(newData.get(entity).size());
                    newData.get(entity).forEach(entry -> {
                        String uniqueValue = entry.get(property.getName()) == null ? null : entry.get(property.getName()).toString();
                        if (uniqueFieldValues.contains(uniqueValue)) {
                            throw new UniqueConstraintException(entity, property.getName(), uniqueValue);
                        }
                        uniqueFieldValues.add(uniqueValue);
                    });
                });
    }

    private void checkDataTypes(XmlModel model, String entity) {
        XmlModelClass modelClass = model.getClass(entity);
        newData.get(entity).forEach(fields -> {
            // id to be able to point out the error.
            String id = fields.get(ID_FIELD).toString();
            fields.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals(ID_FIELD))
                    .forEach(entry -> {
                        XmlModelClassProperty property = modelClass.getPropertyWithHierarchyNullable(entry.getKey());
                        if (property == null) {
                            throw new PropertyNotFoundInClassException(modelClass, entry.getKey());
                        }
                        TypeChecker typeChecker;
                        if (isPrimitiveType(property.getType())) {
                            switch (property.getType().toLowerCase(Locale.ENGLISH)) {
                                case "string":
                                    typeChecker = new StringChecker(id, entry.getKey(), entity, property);
                                    break;
                                case "integer":
                                    typeChecker = new IntegerChecker(id, entry.getKey(), entity);
                                    break;
                                case "long":
                                    typeChecker = new LongChecker(id, entry.getKey(), entity);
                                    break;
                                case "short":
                                    typeChecker = new ShortChecker(id, entry.getKey(), entity);
                                    break;
                                case "byte":
                                    typeChecker = new ByteChecker(id, entry.getKey(), entity);
                                    break;
                                case "bigdecimal":
                                    typeChecker = new BigDecimalChecker(id, entry.getKey(), entity, property);
                                    break;
                                case "boolean":
                                    typeChecker = new BooleanChecker(id, entry.getKey(), entity);
                                    break;
                                case "character":
                                    typeChecker = new CharacterChecker(id, entry.getKey(), entity);
                                    break;
                                case "float":
                                    typeChecker = new FloatChecker(id, entry.getKey(), entity);
                                    break;
                                case "double":
                                    typeChecker = new DoubleChecker(id, entry.getKey(), entity);
                                    break;
                                case "date":
                                    typeChecker = new DateChecker(id, entry.getKey(), entity);
                                    break;
                                case "localdate":
                                    typeChecker = new LocalDateChecker(id, entry.getKey(), entity);
                                    break;
                                case "localdatetime":
                                    typeChecker = new LocalDateTimeChecker(id, entry.getKey(), entity);
                                    break;
                                default:
                                    typeChecker = new EmptyChecker();
                            }
                        } else if (isEnum(property)) {
                            typeChecker = new EnumChecker(id, entry.getKey(), entity, property);
                        } else if (isReference(property)) {
                            typeChecker = new EmptyValueChecker(id, entry.getKey(), entity);
                        } else {
                            typeChecker = new EmptyChecker();
                        }
                        if (property.getCollectionType() != null) {
                            typeChecker = new CollectionChecker(id, entry.getKey(), entity, typeChecker);
                        }
                        typeChecker.check(entry.getValue());
                    });
        });
    }

    private static boolean isReference(XmlModelClassProperty property) {
        XmlModel xmlModel = property.getModelClass().getModel();
        return xmlModel.containsClass(property.getType());
    }

    private static boolean isEnum(XmlModelClassProperty property) {
        XmlModel xmlModel = property.getModelClass().getModel();
        return xmlModel.getEnums().stream().anyMatch(en -> en.getName().equals(property.getType()));
    }

    private void checkConsistency(XmlModel model, String entity) {
        XmlModelClass xmlModelClass = model.getClass(entity);
        Set<XmlModelClassProperty> propertiesToReference = xmlModelClass.getPropertiesAsList().stream()
                .filter(property -> model.containsClass(property.getType()) && !model.getClass(property.getType()).isEmbeddable())
                .collect(Collectors.toSet());

        checkReferencesToDictionaryOnly(model, entity, propertiesToReference);

        checkDictionariesConsistency(entity, propertiesToReference);
    }

    private void checkReferencesToDictionaryOnly(XmlModel model, String entity, Set<XmlModelClassProperty> properties) {
        Optional<XmlModelClass> first = properties.stream()
                .map(property -> model.getClass(property.getType()))
                .filter(modelClass -> !modelClass.isDictionary() && !modelClass.isEmbeddable())
                .findFirst();
        if (first.isPresent()) {
            throw new NotDictionaryReferenceException(entity, first.get().getName());
        }
    }

    private void checkDictionariesConsistency(String entity, Set<XmlModelClassProperty> propertiesToReference) {
        newData.get(entity).forEach(map ->
                propertiesToReference.forEach(property ->
                        Optional.ofNullable(map.get(property.getName()))
                            .ifPresent(value -> {
                                List<Map<String, Object>> objectsPropertyType = newData.get(property.getType());
                                if (objectsPropertyType == null) {
                                    throwDictionaryConsistencyException(entity, property, value);
                                    return;
                                }
                                // toString-и нужен для корректного сравнения.
                                // The type can be String or Integer depending on how the id is specified in the json("1" / 1).
                                boolean isReferenceValid = objectsPropertyType.stream()
                                    .anyMatch(propertyMap ->
                                        propertyMap.get(ID_FIELD).toString().equals(value.toString()));
                                if (!isReferenceValid) {
                                    throwDictionaryConsistencyException(entity, property, value);
                                }
                            })
                )
        );
    }

    private void throwDictionaryConsistencyException(String entity, XmlModelClassProperty property, Object value) {
        throw new DictionaryConsistencyException(
                entity,
                property.getName(),
                property.getType(),
                value.toString());
    }

    private void checkNoDeleteObjects() {
        oldData.forEach((entity, oldObjects) ->
                oldObjects.forEach(values -> {
                    Object id = values.get(ID_FIELD);
                    List<Map<String, Object>> newObjects = newData.get(entity);
                    if (newObjects == null || newObjects.isEmpty()) {
                        throw new DictionaryDeleteException(entity, id);
                    }
                    boolean exists = false;
                    for (Map<String, Object> object : newObjects) {
                        if (object.get(ID_FIELD) != null && object.get(ID_FIELD).equals(id)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        throw new DictionaryDeleteException(entity, id);
                    }
                }));

    }

    @Override
    public Map<String, List<Map<String, Object>>> getAddData() {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();

        newData.forEach((entity, fields) ->
            fields.forEach(newMap -> {
                Optional<Map<String, Object>> oldObject = getOldObjectData(entity, newMap.get(ID_FIELD));
                if (!oldObject.isPresent()) {
                    result.putIfAbsent(entity, new ArrayList<>());
                    result.get(entity).add(newMap);
                }
            })
        );

        return result;
    }

    public Optional<Map<String, Object>> getOldObjectData(String entity, Object id) {
        return Optional.ofNullable(oldData.get(entity)).orElse(Collections.emptyList())
                .stream()
                .filter(oldMap -> oldMap.get(ID_FIELD).equals(id))
                .findFirst();
    }

    @Override
    public Map<String, List<Map<String, Object>>> getUpdateData() {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();

        newData.forEach((entity, fields) ->
                fields.forEach(newDataMap -> {
                    Object entityId = newDataMap.get(ID_FIELD);
                    Optional<Map<String, Object>> oldObject = getOldObjectData(entity, entityId);

                    if (!oldObject.isPresent()) {
                        return;
                    }
                    boolean changed = false;
                    Map<String, Object> oldDataMap = oldObject.get();
                    for (Map.Entry<String, Object> entry : oldDataMap.entrySet()) {
                        if (Objects.isNull(newDataMap.get(entry.getKey())) && !Objects.isNull(entry.getValue())) {
                            newDataMap.put(entry.getKey(), null);
                            changed = true;
                        }
                    }

                    for (Map.Entry<String, Object> entry : newDataMap.entrySet()) {
                        if (!Objects.equals(entry.getValue(), oldDataMap.get(entry.getKey()))) {
                            changed = true;
                            break;
                        }
                    }
                    if (changed) {
                        result.putIfAbsent(entity, new ArrayList<>());
                        result.get(entity).add(newDataMap);
                    }

                }));

        return result;
    }

    private Map<String, List<Map<String, Object>>> readData(File directory) {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        if (!directory.exists()) {
            return result;
        }
        wrap(() -> Files.find(directory.toPath(),
                Integer.MAX_VALUE,
                (path, basicFileAttributes) -> basicFileAttributes.isRegularFile() &&
                        path.getFileName().toFile().getName().endsWith(JSON_EXTENSION))
                .forEach(path -> addDictionariesFromFile(path, result)));

        return result;
    }

    private void addDictionariesFromFile(Path path, Map<String, List<Map<String, Object>>> result) {
        JsonData jsonData;
        try {
            File jsonFile = path.toFile();
            jsonData = mapper.readValue(jsonFile, JsonData.class);
            if (Objects.isNull(jsonData.getType())) {
                jsonData.setType(jsonFile.getName().split("\\.")[0]);
            }
        } catch (JsonParseException | JsonMappingException ex) {
            throw new ParseException(path.toFile().getName(), ex);
        } catch (IOException ex) {
            throw new ExecuteException(ex);
        }

        if (!result.containsKey(jsonData.getType())) {
            result.put(jsonData.getType(), new ArrayList<>());
        }
        result.get(jsonData.getType()).addAll(jsonData.getObjects());
    }

    private Map<String, List<Map<String, Object>>> readDataFromFiles(Collection<File> files) {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        files.forEach(file -> addDictionariesFromFile(file.toPath(), result));
        return result;
    }

    private Map<String, List<Map<String, Object>>> readData(Collection<InputStream> streams) {
        HashMap<String, List<Map<String, Object>>> result = new HashMap<>();

        streams.forEach(stream -> {
            JsonData jsonData;
            try {
                jsonData = mapper.readValue(stream, JsonData.class);
            } catch (JsonParseException | JsonMappingException ex) {
                throw new ParseException("stream", ex);
            } catch (IOException ex) {
                throw new ExecuteException(ex);
            }
            if (!result.containsKey(jsonData.getType())) {
                result.put(jsonData.getType(), new ArrayList<>());
            }
            result.get(jsonData.getType()).addAll(jsonData.getObjects());
        });
        return result;
    }

    @Override
    public Map<String, List<Map<String, Object>>> getAllData() {
        return newData;
    }

    @Override
    public Map<String, List<Map<String, Object>>> getOldData() {
        return oldData;
    }

    @Override
    public Set<String> getAllTypes() {
        return Stream.concat(oldData.keySet().stream(), newData.keySet().stream()).collect(Collectors.toSet());
    }

    @Override
    public void clearOldData() {
        this.oldData = Collections.emptyMap();
    }
}
