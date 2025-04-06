package sbp.com.sbt.dataspace.graphqlschema;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Scalar type OffsetDateTime handler
 */
class OffsetDateTimeScalarTypeCoercing extends AbstractJodaTimeScalarTypeCoercing<OffsetDateTime> {

    OffsetDateTimeScalarTypeCoercing() {
        super(DateTimeFormatter.ISO_OFFSET_DATE_TIME, GraphQLSchemaHelper.OFFSET_DATETIME_SCALAR_TYPE_NAME, OffsetDateTime.class, OffsetDateTime::from);
    }
}
