package com.sbt.dictionary;

import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.parameters.dto.DictionaryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DictionaryUtils {

    private DictionaryUtils() {
    }

    public static Collection<DictObject> makeDictObjectGraph(List<DictionaryDto> income, XmlModel model) {
        List<DictionaryDto> workList = new ArrayList<>(income);
        Map<DictObject, DictObject> result = new HashMap<>();

        workList.forEach(dictionaryDto -> {
            final XmlModelClass dictionaryClass = model.getClass(dictionaryDto.getType());
            final String type = dictionaryClass.getName();
            final Collection<TypeName> propertyDictionaryTypes = getPropertyDictionaryTypes(dictionaryClass, model);

            dictionaryDto.getObjects().forEach(json -> {
                final Object id = json.get("id");
                final DictObject dictElement;
                DictObject nextElement = new DictObject(type, id);
                if (result.containsKey(nextElement)) {
                    dictElement = result.get(nextElement);
                } else {
                    dictElement = nextElement;
                    result.put(dictElement, dictElement);
                }

                propertyDictionaryTypes
                        .forEach(typeName -> {
                            final String name = typeName.getName();
                            final Object dictReference = json.get(name);
                            if (dictReference != null) {
                                final DictObject depends = new DictObject(typeName.getType(), dictReference);
                                if (result.containsKey(depends)) {
                                    dictElement.getDepends().add(result.get(depends));
                                } else {
                                    dictElement.getDepends().add(depends);
                                    result.put(depends, depends);
                                }

                            }
                        });
            });
        });

        return result.values();
    }

    private static Collection<TypeName> getPropertyDictionaryTypes(XmlModelClass modelClass, XmlModel model) {
        return modelClass.getPropertiesAsList().stream()
                .filter(property -> {
                    if (property.getMappedBy() != null) {
                        return false;
                    }
                    final XmlModelClass propertyClass = model.getClassNullable(property.getType());
                    return propertyClass != null && propertyClass.isDictionary();
                })
                .map(it -> new TypeName(it.getType(), it.getName()))
                .collect(Collectors.toSet());
    }

    @AllArgsConstructor
    @Getter
    private static class TypeName {
        private final String type;
        private final String name;
    }
}
