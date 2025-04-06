package com.sbt.model.common;

import com.google.common.base.Strings;
import com.sbt.mg.data.model.Property;
import com.sbt.model.cci.exceptions.CciIndexAlreadyDefineException;
import com.sbt.model.common.exceptions.CciIndexTrimEnoughException;
import com.sbt.model.common.exceptions.TrimEnoughNameException;
import com.sbt.model.index.exception.EmptyIndexException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IndexNamer {

    private static final int initialValue = 1;

    private final Map<String, Integer> indexNames = new HashMap<>();
    private final String prefix;
    private final int minEntityLength;
    private final int maxNameLength;

    public IndexNamer(int minEntityLength, int maxNameLength) {
        this.prefix = "";
        this.minEntityLength = minEntityLength;
        this.maxNameLength = maxNameLength;
    }

    public IndexNamer(String prefix, int minEntityLength, int maxNameLength) {
        this.prefix = prefix;
        this.minEntityLength = minEntityLength;
        this.maxNameLength = maxNameLength;
    }

    public String generateName(String className, List<Property> properties) {
        if (properties.isEmpty()) {
            throw new EmptyIndexException(className);
        }
        String name = StringUtils.upperCase(className) + "_" +
                StringUtils.upperCase(properties.get(0).getName().replace(".", ""));
        if (indexNames.containsKey(name)) {
            if (properties.size() != 1) {
                if (indexNames.get(name) == null) {
                    indexNames.put(name, initialValue);
                } else {
                    indexNames.put(name, indexNames.get(name) + 1);
                }
            } else {
                throw new CciIndexAlreadyDefineException(properties.get(0).getName(), className);
            }
        } else {
            indexNames.put(name, properties.size() == 1 ? null : initialValue);
        }

        try {
            return trimName(Arrays.asList(
                    prefix,
                    StringUtils.upperCase(className),
                    StringUtils.upperCase(properties.get(0).getName().replace(".", "")),
                    indexNames.get(name) == null ? "" : indexNames.get(name).toString()));
        } catch (TrimEnoughNameException ex) {
            throw new CciIndexTrimEnoughException(
                    className,
                    properties.stream().map(Property::getName).collect(Collectors.toList()));
        }

    }

    /**
     * Forming the index name to be less than maxNameLength, but in such a way that the elements included in the name contained not less
     * minEntityLength for sense preservation.
     *
     * @param inNameElements name components
     * @return name length less than maxNameLength
     */
    private String trimName(List<String> inNameElements) {
        List<String> nameElements = inNameElements.stream()
                .filter(it -> !Strings.isNullOrEmpty(it))
                .collect(Collectors.toList());
        String resultName;
        String possibleName = String.join("_", nameElements);
        List<String> trimmedElements = new ArrayList<>(nameElements.size());
        if (needTrimName(possibleName)) {
            int trimLength = possibleName.length() - this.maxNameLength;
            for (int i = nameElements.size() - 1; i >= 0; i--) {
                String currentElement = nameElements.get(i);
                if (needTrim(trimLength)) {
                    if (canTrimElement(currentElement)) {
                        int possibleTrimLength = currentElement.length() - this.minEntityLength;
                        if (possibleTrimLength > 0) {
                            //There is something to cut from the current element.
                            if (possibleTrimLength > trimLength) {
                                //The current element has enough excess length after trimming to fit the entire name within the limit.
                                trimmedElements.add(i, currentElement.substring(0, trimLength));
                                trimLength = 0; // no more cutting is needed
                            } else {
                                trimmedElements.add(i, currentElement.substring(0, possibleTrimLength));
                                trimLength -= possibleTrimLength;
                            }
                        } else {
                            trimmedElements.add(i, currentElement);
                        }
                    }
                } else {
                    trimmedElements.add(i, currentElement);
                }
            }

            resultName = String.join("_", trimmedElements);
        } else {
            resultName = possibleName;
        }

        if (needTrimName(resultName)) {
            throw new TrimEnoughNameException(nameElements);
        }
        return resultName;
    }

    private boolean needTrimName(String name) {
        return name.length() > this.maxNameLength;
    }

    private boolean needTrim(int trimLength) {
        return trimLength > 0;
    }

    private boolean canTrimElement(String element) {
        return element.length() > this.minEntityLength;
    }
}
