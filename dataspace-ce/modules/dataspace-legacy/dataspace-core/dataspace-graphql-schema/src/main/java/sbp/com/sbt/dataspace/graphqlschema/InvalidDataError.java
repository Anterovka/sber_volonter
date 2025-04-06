package sbp.com.sbt.dataspace.graphqlschema;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.List;

/**
 * Invalid data error
 */
public class InvalidDataError implements GraphQLError {

    String id;
    String type;
    List<Object> path;

    /**
     * @param id   Id
     * @param type Тип
     * @param path Path
     */
    public InvalidDataError(String id, String type, List<Object> path) {
        this.id = id;
        this.type = type;
        this.path = path;
    }

    @Override
    public String getMessage() {
        return "Reference of type " + type + " with id = " + id + " is invalid";
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorClassification getErrorType() {
        return ErrorType.INVALID_DATA;
    }

    @Override
    public List<Object> getPath() {
        return path;
    }
}
