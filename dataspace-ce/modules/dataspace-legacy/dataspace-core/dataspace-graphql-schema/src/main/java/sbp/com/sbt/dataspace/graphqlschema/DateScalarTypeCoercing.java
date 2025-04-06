package sbp.com.sbt.dataspace.graphqlschema;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Scalar type date handler
 */
class DateScalarTypeCoercing extends AbstractJodaTimeScalarTypeCoercing<LocalDate> {

    DateScalarTypeCoercing() {
        super(DateTimeFormatter.ISO_DATE, GraphQLSchemaHelper.DATE_SCALAR_TYPE_NAME, LocalDate.class, LocalDate::from);
    }
}
