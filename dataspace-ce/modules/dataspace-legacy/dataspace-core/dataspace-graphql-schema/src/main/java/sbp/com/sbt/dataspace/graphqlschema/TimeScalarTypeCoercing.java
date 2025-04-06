package sbp.com.sbt.dataspace.graphqlschema;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Scalar time handler
 */
class TimeScalarTypeCoercing extends AbstractJodaTimeScalarTypeCoercing<LocalTime> {

    TimeScalarTypeCoercing() {
        super(DateTimeFormatter.ISO_TIME, GraphQLSchemaHelper.TIME_SCALAR_TYPE_NAME, LocalTime.class, LocalTime::from);
    }
}
