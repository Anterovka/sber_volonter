package sbp.com.sbt.dataspace.graphqlschema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Scalar type handler DateTime
 */
class DateTimeScalarTypeCoercing extends AbstractJodaTimeScalarTypeCoercing<LocalDateTime> {

    DateTimeScalarTypeCoercing() {
        super(DateTimeFormatter.ISO_DATE_TIME, GraphQLSchemaHelper.DATETIME_SCALAR_TYPE_NAME, LocalDateTime.class, LocalDateTime::from);
    }
}
