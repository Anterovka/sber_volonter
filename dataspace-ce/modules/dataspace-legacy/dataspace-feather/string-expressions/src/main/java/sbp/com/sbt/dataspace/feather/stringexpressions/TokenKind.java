package sbp.com.sbt.dataspace.feather.stringexpressions;

import java.util.function.Predicate;

/**
 * Token type
 */
enum TokenKind {

    /**
     * ||
     */
    OR("||"),
    /**
     * &&
     */
    AND("&&"),
    /**
     * !
     */
    NOT("!"),
    /**
     * ==
     */
    EQ("=="),
    /**
     * !=
     */
    NOT_EQ("!="),
    /**
     * >
     */
    GT(">"),
    /**
     * <=
     */
    LT_OR_EQ("<="),
    /**
     * <
     */
    LT("<"),
    /**
     * >=
     */
    GT_OR_EQ(">="),
    /**
     * $like
     */
    LIKE("$like"),
    /**
     * $between
     */
    BETWEEN("$between"),
    /**
     * $in
     */
    IN("$in"),
    /**
     * null
     */
    NULL("null"),
    /**
     * (
     */
    BRACKET_L("("),
    /**
     * )
     */
    BRACKET_R(")"),
    /**
     * {
     */
    BRACKET2_L("{"),
    /**
     * }
     */
    BRACKET2_R("}"),
    /**
     * [
     */
    BRACKET3_L("["),
    /**
     * ]
     */
    BRACKET3_R("]"),
    /**
     * (
     */
    COMMA(","),
    /**
     * +
     */
    PLUS("+"),
    /**
     * -
     */
    MINUS("-"),
    /**
     * *
     */
    MUL("*"),
    /**
     * /
     */
    DIV("/"),
    /**
     * '(''|(?!').)*'
     */
    STRING("${строка}", TokenParser::parseString),
    /**
     * [0-9]+(\.[0-9]+)?
     */
    NUMBER("${число}", TokenParser::parseNumber),
    /**
     * Date in ISO-8601 format with prefix D
     *
     * @see java.time.format.DateTimeFormatter#ISO_LOCAL_DATE
     */
    DATE("${дата}", TokenParser::parseDate),
    /**
     * Date and time in ISO-8601 format with prefix D
     *
     * @see java.time.format.DateTimeFormatter#ISO_LOCAL_DATE_TIME
     */
    DATETIME("${date and time}", TokenParser::parseDateTime),
    /**
     * Date and time offset in ISO-8601 format with prefix D
     *
     * @see java.time.format.DateTimeFormatter#ISO_OFFSET_DATE_TIME
     */
    OFFSET_DATETIME("${offset datetime with adjustment}", TokenParser::parseOffsetDateTime),
    /**
     * Time in ISO-8601 format with T prefix
     *
     * @see java.time.format.DateTimeFormatter#ISO_LOCAL_TIME
     */
    TIME("${время}", TokenParser::parseTime),
    /**
     * true
     */
    TRUE("true"),
    /**
     * false
     */
    FALSE("false"),
    /**
     * it
     */
    IT("it"),
    /**
     * root
     */
    ROOT("root"),
    /**
     * elem
     */
    ELEM("elem"),
    /**
     * @
     */
    AT("@"),
    /**
     * entities
     */
    ENTITIES("entities"),
    /**
     * [A-Za-z0-9_]+
     */
    IDENTIFIER("${идентификатор}", TokenParser::parseIdentifier),
    /**
     * type
     */
    TYPE("type"),
    /**
     * alias
     */
    ALIAS("alias"),
    /**
     * elemAlias
     */
    ELEM_ALIAS("elemAlias"),
    /**
     * cond
     */
    COND("cond"),
    /**
     * =
     */
    ASSIGN("="),
    /**
     * .
     */
    DOT("."),
    /**
     * $
     */
    DOLLAR("$"),
    /**
     * upper
     */
    UPPER("upper"),
    /**
     * lower
     */
    LOWER("lower"),
    /**
     * length
     */
    LENGTH("length"),
    /**
     * trim
     */
    TRIM("trim"),
    /**
     * ltrim
     */
    LTRIM("ltrim"),
    /**
     * rtrim
     */
    RTRIM("rtrim"),
    /**
     * round
     */
    ROUND("round"),
    /**
     * ceil
     */
    CEIL("ceil"),
    /**
     * floor
     */
    FLOOR("floor"),
    /**
     * hash
     */
    HASH("hash"),
    /**
     * asString
     */
    AS_STRING("asString"),
    /**
     * asBigDecimal
     */
    AS_BIG_DECIMAL("asBigDecimal"),
    /**
     * asDate
     */
    AS_DATE("asDate"),
    /**
     * asDateTime
     */
    AS_DATE_TIME("asDateTime"),
    /**
     * asOffsetDateTime
     */
    AS_OFFSET_DATE_TIME("asOffsetDateTime"),
    /**
     * asTime
     */
    AS_TIME("asTime"),
    /**
     * year
     */
    YEAR("year"),
    /**
     * month
     */
    MONTH("month"),
    /**
     * day
     */
    DAY("day"),
    /**
     * hour
     */
    HOUR("hour"),
    /**
     * minute
     */
    MINUTE("minute"),
    /**
     * second
     */
    SECOND("second"),
    /**
     * offsetHour
     */
    OFFSET_HOUR("offsetHour"),
    /**
     * offsetMinute
     */
    OFFSET_MINUTE("offsetMinute"),
    /**
     * date
     */
    DATE2("date"),
    /**
     * time
     */
    TIME2("time"),
    /**
     * dateTime
     */
    DATETIME2("dateTime"),
    /**
     * offset
     */
    OFFSET("offset"),
    /**
     * abs
     */
    ABS("abs"),
    /**
     * substr
     */
    SUBSTR("substr"),
    /**
     * replace
     */
    REPLACE("replace"),
    /**
     * coalesce
     */
    COALESCE("coalesce"),
    /**
     * now
     */
    NOW("now"),
    /**
     * addMilliseconds
     */
    ADD_MILLISECONDS("addMilliseconds"),
    /**
     * addSeconds
     */
    ADD_SECONDS("addSeconds"),
    /**
     * addMinutes
     */
    ADD_MINUTES("addMinutes"),
    /**
     * addHours
     */
    ADD_HOURS("addHours"),
    /**
     * addDays
     */
    ADD_DAYS("addDays"),
    /**
     * addMonths
     */
    ADD_MONTHS("addMonths"),
    /**
     * addYears
     */
    ADD_YEARS("addYears"),
    /**
     * subMilliseconds
     */
    SUB_MILLISECONDS("subMilliseconds"),
    /**
     * subSeconds
     */
    SUB_SECONDS("subSeconds"),
    /**
     * subMinutes
     */
    SUB_MINUTES("subMinutes"),
    /**
     * subHours
     */
    SUB_HOURS("subHours"),
    /**
     * subDays
     */
    SUB_DAYS("subDays"),
    /**
     * addMonths
     */
    SUB_MONTHS("subMonths"),
    /**
     * addYears
     */
    SUB_YEARS("subYears"),
    /**
     * id
     */
    ID("id"),
    /**
     * min
     */
    MIN("min"),
    /**
     * max
     */
    MAX("max"),
    /**
     * sum
     */
    SUM("sum"),
    /**
     * avg
     */
    AVG("avg"),
    /**
     * count
     */
    COUNT("count"),
    /**
     * exists
     */
    EXISTS("exists"),
    /**
     * $mod
     */
    MOD("$mod"),
    /**
     * %
     */
    MOD2("%"),
    /**
     * asBigDecimal
     */
    AS_BOOLEAN("asBoolean"),
    /**
     * map
     */
    MAP("map"),
    /**
     * params
     */
    PARAMS("params"),
    /**
     * |
     */
    BIT_OR("|"),
    /**
     * |
     */
    BIT_XOR("^"),
    /**
     * |
     */
    BIT_AND("&"),
    /**
     * ~
     */
    BIT_NOT("~"),
    /**
     * <<
     */
    SHIFT_LEFT("<<"),
    /**
     * <<
     */
    SHIFT_RIGHT(">>"),
    /**
     * lpad
     */
    LPAD("lpad"),
    /**
     * rpad
     */
    RPAD("rpad"),
    /**
     * any
     */
    ANY("any"),
    /**
     * all
     */
    ALL("all"),
    /**
     * power
     */
    POWER("power"),
    /**
     * log
     */
    LOG("log");

    String image;
    Predicate<TokenParser> parseMethod;

    /**
     * @param image         Image
     * @param parseMethod Parsing method
     */
    TokenKind(String image, Predicate<TokenParser> parseMethod) {
        this.image = image;
        this.parseMethod = parseMethod;
    }

    /**
     * @param image Image
     */
    TokenKind(String image) {
        this(image, tokenParser -> tokenParser.parseSimpleToken(image));
    }

    @Override
    public String toString() {
        return image;
    }
}
