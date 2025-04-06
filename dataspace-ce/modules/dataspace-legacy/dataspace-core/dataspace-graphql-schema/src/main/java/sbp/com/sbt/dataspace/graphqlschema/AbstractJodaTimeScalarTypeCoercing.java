package sbp.com.sbt.dataspace.graphqlschema;

import graphql.language.StringValue;
import graphql.schema.Coercing;

import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;

import static sbp.com.sbt.dataspace.graphqlschema.Helper.getCoercingParseLiteralException;
import static sbp.com.sbt.dataspace.graphqlschema.Helper.getCoercingParseValueException;
import static sbp.com.sbt.dataspace.graphqlschema.Helper.getCoercingSerializeValueException;

/**
 * Basic handler of scalar date and time types
 */
class AbstractJodaTimeScalarTypeCoercing<T extends Temporal> implements Coercing<T, String> {
    private final DateTimeFormatter formatter;
    private final String scalarName;
    private final Class<T> type;
    private final TemporalQuery<T> temporalMapper;
    private boolean suppressCoercingErrors = false;

    AbstractJodaTimeScalarTypeCoercing(DateTimeFormatter formatter, String scalarName, Class<T> type, TemporalQuery<T> temporalMapper) {
        this.formatter = formatter;
        this.scalarName = scalarName;
        this.type = type;
        this.temporalMapper = temporalMapper;
    }

    @Override
    final public String serialize(Object dataFetcherResult) {
        if (suppressCoercingErrors) {
            return (String) dataFetcherResult;
        } else {
            try {
                TemporalAccessor temporalAccessor;
                if (dataFetcherResult instanceof TemporalAccessor) {
                    temporalAccessor = (TemporalAccessor) dataFetcherResult;
                } else if (dataFetcherResult instanceof String) {
                    temporalAccessor = formatter.parse((String) dataFetcherResult);
                } else {
                    throw getCoercingSerializeValueException(scalarName, dataFetcherResult);
                }
                return formatter.format(temporalAccessor);
            } catch (DateTimeException e) {
                throw getCoercingSerializeValueException(scalarName, dataFetcherResult);
            }
        }
    }

    @Override
    public T parseValue(Object input) {
        if (type.isInstance(input)) {
            return (T) input;
        } else if (input instanceof String) {
            try {
                return formatter.parse((String) input, temporalMapper);
            } catch (Exception e) {
                //No action required
            }
        }
        throw getCoercingParseValueException(scalarName, input);
    }

    @Override
    public T parseLiteral(Object input) {
        if (input instanceof StringValue) {
            try {
                return formatter.parse(((StringValue) input).getValue(), temporalMapper);
            } catch (Exception e) {
                //No action required
            }
        }
        throw getCoercingParseLiteralException(scalarName, input);
    }

    public boolean isSuppressCoercingErrors() {
        return suppressCoercingErrors;
    }

    public void setSuppressCoercingErrors(boolean suppressCoercingErrors) {
        this.suppressCoercingErrors = suppressCoercingErrors;
    }
}
