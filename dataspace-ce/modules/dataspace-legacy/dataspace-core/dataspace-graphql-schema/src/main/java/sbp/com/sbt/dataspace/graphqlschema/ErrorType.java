package sbp.com.sbt.dataspace.graphqlschema;

import graphql.ErrorClassification;
import graphql.GraphQLError;

/**
 * Type of error
 */
enum ErrorType implements ErrorClassification {

    /**
     * Invalid data
     */
    INVALID_DATA("InvalidData");

    final String classification;

    /**
     * @param classification Classification
     */
    ErrorType(String classification) {
        this.classification = classification;
    }

    @Override
    public Object toSpecification(GraphQLError error) {
        return classification;
    }
}
