package com.sbt.model.exception;

import com.sbt.mg.data.model.ClassStrategy;
import com.sbt.mg.data.model.Property;
import com.sbt.mg.data.model.XmlIndex;
import com.sbt.model.exception.parent.CheckModelException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Exception: The index name is already set in the model
 */
public class IndexNameAlreadyDefinedException extends CheckModelException {
    /**
     * @param indexName Index name
     */
    public IndexNameAlreadyDefinedException(String indexName) {
        super(String.format("The index name %s is already set in the model class", indexName),
            "The repetition of index names needs to be eliminated.");
    }

    private IndexNameAlreadyDefinedException(String errorMessage, String solutionMessage) {
        super(errorMessage, solutionMessage);
    }

    public static IndexNameAlreadyDefinedException ofIndexNames(Set<String> indexNames) {
        return new IndexNameAlreadyDefinedException(
            String.format("In the model, the following index names are found to be duplicated: %s", indexNames),
            "It is necessary to eliminate repetitions of index names."
        );
    }

    public IndexNameAlreadyDefinedException(Set<XmlIndex> repeatedIndices) {
        super("Found indices with duplicate names: [" + getIndexInfo(repeatedIndices) + "]",
            join("It is necessary to eliminate repetitions of index names. With the inheritance strategy",
                ClassStrategy.SINGLE_TABLE, "indices for different classes belong to the same table."));
    }

    private static String getIndexInfo(Set<XmlIndex> repeatedIndices) {
        return repeatedIndices.stream()
            .map(it -> String.join(" ",
                "Index", it.getIndexName(), "with field composition",
                getPropertiesInfo(it.getProperties()), "in class", it.getModelClass().getName()))
            .collect(Collectors.joining(", "));
    }

    private static String getPropertiesInfo(List<Property> properties) {
        return "[" + properties.stream()
                .map(Property::getName)
                .collect(Collectors.joining(",")) + "]";
    }
}
