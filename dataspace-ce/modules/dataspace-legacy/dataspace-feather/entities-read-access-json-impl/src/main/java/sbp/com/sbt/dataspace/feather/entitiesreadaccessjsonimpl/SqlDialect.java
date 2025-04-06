package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.addNodeListToNodes;
import static sbp.com.sbt.dataspace.feather.common.Node.node;

/**
 * SQL Dialect
 */
public enum SqlDialect {

    /**
     * PostgreSQL
     */
    POSTGRESQL {
        @Override
        Node<String> null0(DataType type) {
            return node(Helper.NULL_NODE, type.getMetaDataManager().get(DataTypeMetaData.class).postgresqlCastNode);
        }

        @Override
        Node<String> substr(Node<String> stringNode, Node<String> positionNode, Node<String> lengthNode) {
            return node(Helper.SUBSTR_BRACKET_L_NODE, stringNode, Helper.COMMA_BRACKET_L_NODE, positionNode, Helper.BRACKET_R_INT_NODE, Helper.COMMA_BRACKET_L_NODE, lengthNode, Helper.BRACKET_R_INT_NODE, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> substr(Node<String> stringNode, Node<String> positionNode) {
            return node(Helper.SUBSTR_BRACKET_L_NODE, stringNode, Helper.COMMA_BRACKET_L_NODE, positionNode, Helper.BRACKET_R_INT_NODE, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> hash(Node<String> node) {
            return node(Helper.HASH_TEXT_BRACKET_L_NODE, node, Helper.BRACKET_R_TEXT_BRACKET_R_NODE);
        }

        @Override
        Node<String> processParameterNode(Node<String> parameterNode, DataType type) {
            return node(parameterNode, type.getMetaDataManager().get(DataTypeMetaData.class).postgresqlCastNode);
        }

        @Override
        Node<String> castStringAsOffsetDateTime(Node<String> offsetDateTimeNode) {
            return node(Helper.TO_TIMESTAMP_NODE, offsetDateTimeNode, Helper.OFFSET_DATETIME_FORMAT_BRACKET_R_NODE);
        }

        @Override
        Node<String> lpad(Node<String> stringNode, Node<String> paddedLengthNode, Node<String> padStringNode) {
            return node(Helper.LPAD_NODE, stringNode, Helper.COMMA_NODE, paddedLengthNode, Helper.INT4_NODE, Helper.COMMA_NODE, padStringNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> rpad(Node<String> stringNode, Node<String> paddedLengthNode, Node<String> padStringNode) {
            return node(Helper.RPAD_NODE, stringNode, Helper.COMMA_NODE, paddedLengthNode, Helper.INT4_NODE, Helper.COMMA_NODE, padStringNode, Helper.BRACKET_R_NODE);
        }
    },
    /**
     * Oracle
     */
    ORACLE {
        @Override
        Node<String> rowNumberFunction(List<Node<String>> partitionColumnNodes, List<Node<String>> sortColumnNodes) {
            return super.rowNumberFunction(partitionColumnNodes, sortColumnNodes.isEmpty() ? ONE_SORT_COLUMN_NODE : sortColumnNodes);
        }

        @Override
        Node<String> limitAndOffset(Node<String> limitNode, Node<String> offsetNode) {
            List<Node<String>> nodes = new ArrayList<>(6);
            if (offsetNode != null) {
                nodes.add(Helper.OFFSET_NODE);
                nodes.add(offsetNode);
                nodes.add(Helper.ROWS_NODE);
            }
            if (limitNode != null) {
                nodes.add(Helper.FETCH_NEXT_NODE);
                nodes.add(limitNode);
                nodes.add(Helper.ROWS_ONLY_NODE);
            }
            return node(nodes);
        }

        @Override
        Node<String> castAsString(Node<String> numberNode) {
            return node(Helper.TO_CHAR_NODE, numberNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> castAsBigDecimal(Node<String> stringNode) {
            return node(Helper.TO_NUMBER_NODE, stringNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> div(Node<String> stringNode1, Node<String> stringNode2) {
            return node(Helper.BRACKET_L_NODE, stringNode1, Helper.DIV_NODE, stringNode2, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> dual() {
            return Helper.DUAL;
        }

        @Override
        Node<String> selectOne() {
            return Helper.SELECT_ONE_FROM_DUAL_NODE;
        }

        @Override
        Node<String> processDate(DataType type, Node<String> stringNode) {
            return type == DataType.DATE ? node(Helper.TO_TIMESTAMP_NODE, stringNode, Helper.BRACKET_R_NODE) : stringNode;
        }

        @Override
        Node<String> castAsBoolean(Node<String> conditionNode) {
            return node(Helper.CASE_WHEN_NODE, conditionNode, Helper.THEN_ONE_ELSE_ZERO_END_NODE);
        }

        @Override
        Node<String> castOffsetDateTimeAsString(Node<String> offsetDateTimeNode) {
            return node(Helper.OFFSET_DATETIME_AS_STRING_PART2_1_NODE, offsetDateTimeNode, Helper.OFFSET_DATETIME_AS_STRING_PART2_2_NODE);
        }

        @Override
        Node<String> castStringAsOffsetDateTime(Node<String> offsetDateTimeNode) {
            return node(Helper.STRING_AS_OFFSET_DATETIME_PART1_NODE, offsetDateTimeNode, Helper.STRING_AS_OFFSET_DATETIME_PART2_NODE);
        }

        @Override
        LocalTime readTime(ResultSet resultSet, int columnIndex) throws SQLException {
            String result = resultSet.getString(columnIndex);
            return result == null ? null : LocalTime.parse(result, DateTimeFormatter.ISO_LOCAL_TIME);
        }

        @Override
        Node<String> castTimeAsString(Node<String> timeNode) {
            return timeNode;
        }

        @Override
        Node<String> castStringAsTime(Node<String> stringNode) {
            return stringNode;
        }

        @Override
        Node<String> getDate(Node<String> dateTimeNode) {
            return node(Helper.TRUNC_NODE, dateTimeNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> getTime(Node<String> dateTimeNode) {
            return node(Helper.TO_CHAR_NODE, dateTimeNode, Helper.CAST_TIME_AS_STRING_NODE);
        }

        @Override
        Node<String> getOffset(Node<String> offsetDateTimeNode) {
            return node(Helper.GET_OFFSET_PART2_1_NODE, offsetDateTimeNode, Helper.GET_OFFSET_PART2_2_NODE);
        }

        @Override
        Node<String> bitNot(Node<String> numberNode) {
            return node(Helper.BIT_NOT_PART1_NODE, numberNode, Helper.BIT_NOT_PART2_NODE);
        }

        @Override
        Node<String> bitAnd(Node<String> number1Node, Node<String> number2Node) {
            return node(Helper.BIT_AND_2_NODE, number1Node, Helper.COMMA_NODE, number2Node, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> bitOr(Node<String> number1Node, Node<String> number2Node) {
            return node(Helper.BRACKET_L_NODE, Helper.BRACKET_L_NODE, number1Node, Helper.PLUS_NODE, number2Node, Helper.BRACKET_R_NODE, Helper.MINUS_NODE, Helper.BIT_AND_2_NODE, number1Node, Helper.COMMA_NODE, number2Node, Helper.BRACKET_R_NODE, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> bitXor(Node<String> number1Node, Node<String> number2Node) {
            return node(Helper.BRACKET_L_NODE, Helper.BRACKET_L_NODE, number1Node, Helper.PLUS_NODE, number2Node, Helper.BRACKET_R_NODE, Helper.MINUS_NODE, Helper.TWO_NODE, Helper.MUL_NODE, Helper.BIT_AND_2_NODE, number1Node, Helper.COMMA_NODE, number2Node, Helper.BRACKET_R_NODE, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> shiftLeft(Node<String> number1Node, Node<String> number2Node) {
            return node(Helper.BRACKET_L_NODE, number1Node, Helper.MUL_NODE, Helper.POWER_NODE, Helper.TWO_NODE, Helper.COMMA_NODE, number2Node, Helper.BRACKET_R_NODE, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> shiftRight(Node<String> number1Node, Node<String> number2Node) {
            return node(Helper.FLOOR_BRACKET_L_NODE, number1Node, Helper.DIV_NODE, Helper.POWER_NODE, Helper.TWO_NODE, Helper.COMMA_NODE, number2Node, Helper.BRACKET_R_NODE, Helper.BRACKET_R_NODE);
        }
    },
    /**
     * H2
     */
    H2 {
        @Override
        SortCriterionData processSortCriterionData(SortCriterionData sortCriterionData) {
            if (sortCriterionData.nullsLast == null) {
                sortCriterionData.nullsLast = !sortCriterionData.descending;
            }
            return sortCriterionData;
        }

        @Override
        Node<String> addMillisecondsToDateTime(Node<String> dateTimeNode, Node<String> millisecondsNode) {
            return node(Helper.DATEADD_MILLISECOND_NODE, millisecondsNode, Helper.COMMA_NODE, dateTimeNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> addSecondsToDateTime(Node<String> dateTimeNode, Node<String> secondsNode) {
            return node(Helper.DATEADD_SECOND_NODE, secondsNode, Helper.COMMA_NODE, dateTimeNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> addMinutesToDateTime(Node<String> dateTimeNode, Node<String> minutesNode) {
            return node(Helper.DATEADD_MINUTE_NODE, minutesNode, Helper.COMMA_NODE, dateTimeNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> addHoursToDateTime(Node<String> dateTimeNode, Node<String> hoursNode) {
            return node(Helper.DATEADD_HOUR_NODE, hoursNode, Helper.COMMA_NODE, dateTimeNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> addDaysToDateTime(Node<String> dateTimeNode, Node<String> daysNode) {
            return node(Helper.DATEADD_DAY_NODE, daysNode, Helper.COMMA_NODE, dateTimeNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> addMonthsToDateTime(Node<String> dateTimeNode, Node<String> monthsNode) {
            return node(Helper.DATEADD_MONTH_NODE, monthsNode, Helper.COMMA_NODE, dateTimeNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> addYearsToDateTime(Node<String> dateTimeNode, Node<String> yearsNode) {
            return node(Helper.DATEADD_YEAR_NODE, yearsNode, Helper.COMMA_NODE, dateTimeNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> subMillisecondsFromDateTime(Node<String> dateNode, Node<String> millisecondsNode) {
            return node(Helper.DATEADD_MILLISECOND_NODE, Helper.MINUS2_NODE, millisecondsNode, Helper.COMMA_NODE, dateNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> subSecondsFromDateTime(Node<String> dateNode, Node<String> secondsNode) {
            return node(Helper.DATEADD_SECOND_NODE, Helper.MINUS2_NODE, secondsNode, Helper.COMMA_NODE, dateNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> subMinutesFromDateTime(Node<String> dateNode, Node<String> minutesNode) {
            return node(Helper.DATEADD_MINUTE_NODE, Helper.MINUS2_NODE, minutesNode, Helper.COMMA_NODE, dateNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> subHoursFromDateTime(Node<String> dateNode, Node<String> hoursNode) {
            return node(Helper.DATEADD_HOUR_NODE, Helper.MINUS2_NODE, hoursNode, Helper.COMMA_NODE, dateNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> subDaysFromDateTime(Node<String> dateNode, Node<String> daysNode) {
            return node(Helper.DATEADD_DAY_NODE, Helper.MINUS2_NODE, daysNode, Helper.COMMA_NODE, dateNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> subMonthsFromDateTime(Node<String> dateNode, Node<String> monthsNode) {
            return node(Helper.DATEADD_MONTH_NODE, Helper.MINUS2_NODE, monthsNode, Helper.COMMA_NODE, dateNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> subYearsFromDateTime(Node<String> dateNode, Node<String> yearsNode) {
            return node(Helper.DATEADD_YEAR_NODE, Helper.MINUS2_NODE, yearsNode, Helper.COMMA_NODE, dateNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> hash(Node<String> node) {
            return node(Helper.ORA_HASH_BRACKET_L_BRACKET_L_NODE, node, Helper.BRACKET_R_TEXT_BRACKET_R_NODE);
        }

        @Override
        Node<String> processIntegerNumber(Node<String> stringNode) {
            return node(Helper.BRACKET_L_NODE, stringNode, Helper.BRACKET_R_DECIMAL_NODE);
        }

        @Override
        Node<String> castOffsetDateTimeAsString(Node<String> offsetDateTimeNode) {
            return node(Helper.OFFSET_DATETIME_AS_STRING_PART1_NODE, offsetDateTimeNode, Helper.OFFSET_DATETIME_AS_STRING_PART2_NODE, offsetDateTimeNode, Helper.OFFSET_DATETIME_AS_STRING_PART3_NODE, offsetDateTimeNode, Helper.OFFSET_DATETIME_AS_STRING_PART4_NODE);
        }

        @Override
        Node<String> getSecond(Node<String> dateTimeNode) {
            return node(Helper.GET_SECOND_PART1_NODE, dateTimeNode, Helper.GET_SECOND_PART2_NODE, dateTimeNode, Helper.GET_SECOND_PART3_NODE);
        }

        @Override
        Node<String> getOffset(Node<String> offsetDateTimeNode) {
            return node(Helper.GET_OFFSET_PART1_NODE, offsetDateTimeNode, Helper.GET_OFFSET_PART2_NODE, offsetDateTimeNode, Helper.GET_OFFSET_PART3_NODE);
        }

        @Override
        Node<String> bitNot(Node<String> numberNode) {
            return node(Helper.BIT_NOT_2_NODE, numberNode, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> bitAnd(Node<String> number1Node, Node<String> number2Node) {
            return node(Helper.BIT_AND_2_NODE, number1Node, Helper.COMMA_NODE, number2Node, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> bitOr(Node<String> number1Node, Node<String> number2Node) {
            return node(Helper.BIT_OR_2_NODE, number1Node, Helper.COMMA_NODE, number2Node, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> bitXor(Node<String> number1Node, Node<String> number2Node) {
            return node(Helper.BIT_XOR_2_NODE, number1Node, Helper.COMMA_NODE, number2Node, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> shiftLeft(Node<String> number1Node, Node<String> number2Node) {
            return node(Helper.SHIFT_LEFT_2_NODE, number1Node, Helper.COMMA_NODE, number2Node, Helper.BRACKET_R_NODE);
        }

        @Override
        Node<String> shiftRight(Node<String> number1Node, Node<String> number2Node) {
            return node(Helper.SHIFT_RIGHT_2_NODE, number1Node, Helper.COMMA_NODE, number2Node, Helper.BRACKET_R_NODE);
        }
    };

    static final List<Node<String>> ONE_SORT_COLUMN_NODE = Collections.singletonList(Helper.ONE_NODE);

    /**
     * Process data sorting criteria
     *
     * @return Sorting criteria data
     */
    SortCriterionData processSortCriterionData(SortCriterionData sortCriterionData) {
        return sortCriterionData;
    }

    /**
     * Get line numbering function node
     *
     * @param partitionColumnNodes Partitioning column nodes
     * @param sortColumnNodes      Sorting columns nodes
     */
    Node<String> rowNumberFunction(List<Node<String>> partitionColumnNodes, List<Node<String>> sortColumnNodes) {
        List<Node<String>> nodes = new ArrayList<>(3 + (partitionColumnNodes.size() + sortColumnNodes.size()) * 2);
        nodes.add(Helper.ROW_NUMBER_OVER_NODE);
        if (!partitionColumnNodes.isEmpty()) {
            nodes.add(Helper.PARTITION_BY_NODE);
            addNodeListToNodes(nodes, Helper.COMMA_NODE, partitionColumnNodes.stream());
        }
        if (!sortColumnNodes.isEmpty()) {
            if (!partitionColumnNodes.isEmpty()) {
                nodes.add(Helper.SPACE_NODE);
            }
            nodes.add(Helper.ORDER_BY2_NODE);
            addNodeListToNodes(nodes, Helper.COMMA_NODE, sortColumnNodes.stream());
        }
        nodes.add(Helper.BRACKET_R_NODE);
        return node(nodes);
    }

    /**
     * Obtain the limit node for the number of elements and offset
     *
     * @param limitNode  Limit node
     * @param offsetNode Parameter node name offset
     */
    Node<String> limitAndOffset(Node<String> limitNode, Node<String> offsetNode) {
        List<Node<String>> nodes = new ArrayList<>(4);
        if (limitNode != null) {
            nodes.add(Helper.LIMIT_NODE);
            nodes.add(limitNode);
        }
        if (offsetNode != null) {
            nodes.add(Helper.OFFSET_NODE);
            nodes.add(offsetNode);
        }
        return node(nodes);
    }

    /**
     * Get null node
     *
     * @param type Тип
     */
    Node<String> null0(DataType type) {
        return Helper.NULL_NODE;
    }

    /**
     * Get node for adding milliseconds to date and time
     *
     * @param dateTimeNode     Time and date node
     * @param millisecondsNode Узел миллисекунд
     */
    Node<String> addMillisecondsToDateTime(Node<String> dateTimeNode, Node<String> millisecondsNode) {
        return node(Helper.BRACKET_L_NODE, dateTimeNode, Helper.PLUS_INTERVAL_MILLISECOND_NODE, millisecondsNode, Helper.BRACKET_R_NODE);
    }

    /**
     * Get node to add seconds to date and time
     *
     * @param dateTimeNode Time and date node
     * @param secondsNode  Seconds node
     */
    Node<String> addSecondsToDateTime(Node<String> dateTimeNode, Node<String> secondsNode) {
        return node(Helper.BRACKET_L_NODE, dateTimeNode, Helper.PLUS_INTERVAL_SECOND_NODE, secondsNode, Helper.BRACKET_R_NODE);
    }

    /**
     * Get the node for adding minutes to date and time
     *
     * @param dateTimeNode Time and date node
     * @param minutesNode  Minutes node
     */
    Node<String> addMinutesToDateTime(Node<String> dateTimeNode, Node<String> minutesNode) {
        return node(Helper.BRACKET_L_NODE, dateTimeNode, Helper.PLUS_INTERVAL_MINUTE_NODE, minutesNode, Helper.BRACKET_R_NODE);
    }

    /**
     * Get the node for adding hours to date and time
     *
     * @param dateTimeNode Time and date node
     * @param hoursNode    Hours node
     */
    Node<String> addHoursToDateTime(Node<String> dateTimeNode, Node<String> hoursNode) {
        return node(Helper.BRACKET_L_NODE, dateTimeNode, Helper.PLUS_INTERVAL_HOUR_NODE, hoursNode, Helper.BRACKET_R_NODE);
    }

    /**
     * Get the node for adding days to date and time
     *
     * @param dateTimeNode Time and date node
     * @param daysNode     Days node
     */
    Node<String> addDaysToDateTime(Node<String> dateTimeNode, Node<String> daysNode) {
        return node(Helper.BRACKET_L_NODE, dateTimeNode, Helper.PLUS_INTERVAL_DAY_NODE, daysNode, Helper.BRACKET_R_NODE);
    }

    /**
     * Obtain the node for adding months to date and time
     *
     * @param dateTimeNode Time and date node
     * @param monthsNode   Months node
     */
    Node<String> addMonthsToDateTime(Node<String> dateTimeNode, Node<String> monthsNode) {
        return node(Helper.BRACKET_L_NODE, dateTimeNode, Helper.PLUS_INTERVAL_MONTH_NODE, monthsNode, Helper.BRACKET_R_NODE);
    }

    /**
     * Obtain the node for adding years to date and time
     *
     * @param dateTimeNode Time and date node
     * @param yearsNode    Year node
     */
    Node<String> addYearsToDateTime(Node<String> dateTimeNode, Node<String> yearsNode) {
        return node(Helper.BRACKET_L_NODE, dateTimeNode, Helper.PLUS_INTERVAL_YEAR_NODE, yearsNode, Helper.BRACKET_R_NODE);
    }

    /**
     * Get the node of milliseconds deducted from the date and time
     *
     * @param dateTimeNode     Time and date node
     * @param millisecondsNode Узел миллисекунд
     */
    Node<String> subMillisecondsFromDateTime(Node<String> dateTimeNode, Node<String> millisecondsNode) {
        return node(Helper.BRACKET_L_NODE, dateTimeNode, Helper.MINUS_INTERVAL_MILLISECOND_NODE, millisecondsNode, Helper.BRACKET_R_NODE);
    }

    /**
     * Get node of seconds deducted from date and time
     *
     * @param dateTimeNode Time and date node
     * @param secondsNode  Seconds node
     */
    Node<String> subSecondsFromDateTime(Node<String> dateTimeNode, Node<String> secondsNode) {
        return node(Helper.BRACKET_L_NODE, dateTimeNode, Helper.MINUS_INTERVAL_SECOND_NODE, secondsNode, Helper.BRACKET_R_NODE);
    }

    /**
     * Get node minutes deducted from date and time
     *
     * @param dateTimeNode Time and date node
     * @param minutesNode  Minutes node
     */
    Node<String> subMinutesFromDateTime(Node<String> dateTimeNode, Node<String> minutesNode) {
        return node(Helper.BRACKET_L_NODE, dateTimeNode, Helper.MINUS_INTERVAL_MINUTE_NODE, minutesNode, Helper.BRACKET_R_NODE);
    }

    /**
     * Get node deduction hours from date and time
     *
     * @param dateTimeNode Time and date node
     * @param hoursNode    Hours node
     */
    Node<String> subHoursFromDateTime(Node<String> dateTimeNode, Node<String> hoursNode) {
        return node(Helper.BRACKET_L_NODE, dateTimeNode, Helper.MINUS_INTERVAL_HOUR_NODE, hoursNode, Helper.BRACKET_R_NODE);
    }

    /**
     * Get node subtract days from date and time
     *
     * @param dateNode Date and time node
     * @param daysNode The node of days
     */
    Node<String> subDaysFromDateTime(Node<String> dateNode, Node<String> daysNode) {
        return node(Helper.BRACKET_L_NODE, dateNode, Helper.MINUS_INTERVAL_DAY_NODE, daysNode, Helper.BRACKET_R_NODE);
    }

    /**
     * Get node of months deducted from date and time
     *
     * @param dateNode   Date and time node
     * @param monthsNode The node of months
     */
    Node<String> subMonthsFromDateTime(Node<String> dateNode, Node<String> monthsNode) {
        return node(Helper.BRACKET_L_NODE, dateNode, Helper.MINUS_INTERVAL_MONTH_NODE, monthsNode, Helper.BRACKET_R_NODE);
    }

    /**
     * Get the year node from the date and time
     *
     * @param dateNode  Date and time node
     * @param yearsNode Year node
     */
    Node<String> subYearsFromDateTime(Node<String> dateNode, Node<String> yearsNode) {
        return node(Helper.BRACKET_L_NODE, dateNode, Helper.MINUS_INTERVAL_YEAR_NODE, yearsNode, Helper.BRACKET_R_NODE);
    }

    /**
     * Get substring node
     *
     * @param stringNode   String node
     * @param positionNode Position node
     * @param lengthNode   Length node
     */
    Node<String> substr(Node<String> stringNode, Node<String> positionNode, Node<String> lengthNode) {
        return node(Helper.SUBSTR_BRACKET_L_NODE, stringNode, Helper.COMMA_NODE, positionNode, Helper.COMMA_NODE, lengthNode, Helper.BRACKET_R_NODE);
    }

    /**
     * Get substring node
     *
     * @param stringNode   String node
     * @param positionNode Position node
     */
    Node<String> substr(Node<String> stringNode, Node<String> positionNode) {
        return node(Helper.SUBSTR_BRACKET_L_NODE, stringNode, Helper.COMMA_NODE, positionNode, Helper.BRACKET_R_NODE);
    }

    /**
     * Get hash
     *
     * @param node Node
     */
    Node<String> hash(Node<String> node) {
        return node(Helper.ORA_HASH_BRACKET_L_NODE, node, Helper.BRACKET_R_NODE);
    }

    /**
     * Get as string
     *
     * @param numberNode Number node
     */
    Node<String> castAsString(Node<String> numberNode) {
        return node(Helper.BRACKET_L_NODE, numberNode, Helper.BRACKET_R_VARCHAR_NODE);
    }

    /**
     * Get as a large decimal number
     *
     * @param stringNode String node
     */
    Node<String> castAsBigDecimal(Node<String> stringNode) {
        return node(Helper.BRACKET_L_NODE, stringNode, Helper.BRACKET_R_DECIMAL_NODE);
    }

    /**
     * Get division
     *
     * @param stringNode2 Узел строки 2
     * @param stringNode2 String node 2
     */
    Node<String> div(Node<String> stringNode1, Node<String> stringNode2) {
        return node(Helper.BRACKET_L_NODE, stringNode1, Helper.BRACKET_R_DECIMAL_DIV_NODE, stringNode2);
    }

    /**
     * Get table dual
     */
    Node<String> dual() {
        return null;
    }

    /**
     * Choose unit
     */
    Node<String> selectOne() {
        return Helper.SELECT_ONE_NODE;
    }

    /**
     * Process date
     *
     * @param type       Type
     * @param stringNode String node
     */
    Node<String> processDate(DataType type, Node<String> stringNode) {
        return stringNode;
    }

    /**
     * Get as a Boolean value
     *
     * @param conditionNode The condition node
     */
    Node<String> castAsBoolean(Node<String> conditionNode) {
        return node(Helper.CASE_WHEN_NODE, conditionNode, Helper.THEN_TRUE_ELSE_FALSE_END_NODE);
    }

    /**
     * Process an integer
     *
     * @param stringNode String node
     */
    Node<String> processIntegerNumber(Node<String> stringNode) {
        return stringNode;
    }

    /**
     * Process parameter node
     *
     * @param parameterNode Parameter node
     * @param type          Тип
     */
    Node<String> processParameterNode(Node<String> parameterNode, DataType type) {
        return parameterNode;
    }

    /**
     * Get date and time with offset as string
     *
     * @param offsetDateTimeNode Node The node for date and time with an offset
     */
    Node<String> castOffsetDateTimeAsString(Node<String> offsetDateTimeNode) {
        return node(Helper.TO_CHAR_NODE, offsetDateTimeNode, Helper.OFFSET_DATETIME_FORMAT_BRACKET_R_NODE);
    }

    /**
     * Get time as a string
     *
     * @param timeNode Time node
     */
    Node<String> castTimeAsString(Node<String> timeNode) {
        return node(Helper.TO_CHAR_NODE, timeNode, Helper.CAST_TIME_AS_STRING_NODE);
    }

    /**
     * Get string as date and time with offset
     *
     * @param offsetDateTimeNode Node The node for date and time with an offset
     */
    Node<String> castStringAsOffsetDateTime(Node<String> offsetDateTimeNode) {
        return node(Helper.TO_TIMESTAMP_TZ_NODE, offsetDateTimeNode, Helper.OFFSET_DATETIME_FORMAT_BRACKET_R_NODE);
    }

    /**
     * Get second
     *
     * @param dateTimeNode Time and date node
     */
    Node<String> getSecond(Node<String> dateTimeNode) {
        return node(Helper.EXTRACT_SECOND_FROM_NODE, dateTimeNode, Helper.BRACKET_R_NODE);
    }

    LocalTime readTime(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getObject(columnIndex, LocalTime.class);
    }

    /**
     * Get string as time
     *
     * @param stringNode String node
     */
    Node<String> castStringAsTime(Node<String> stringNode) {
        return node(Helper.CAST_NODE, stringNode, Helper.AS_TIME_NODE);
    }

    /**
     * Get date
     *
     * @param dateTimeNode Time and date node
     */
    Node<String> getDate(Node<String> dateTimeNode) {
        return node(Helper.CAST_NODE, dateTimeNode, Helper.AS_DATE_NODE);
    }

    /**
     * Get time
     *
     * @param dateTimeNode Time and date node
     */
    Node<String> getTime(Node<String> dateTimeNode) {
        return node(Helper.CAST_NODE, dateTimeNode, Helper.AS_TIME_NODE);
    }

    /**
     * Get offset
     *
     * @param offsetDateTimeNode Node Время и узел данных с учетом часового пояса
     */
    Node<String> getOffset(Node<String> offsetDateTimeNode) {
        return node(Helper.TO_CHAR_NODE, offsetDateTimeNode, Helper.GET_OFFSET_NODE);
    }

    /**
     * Get bitwise negation
     *
     * @param numberNode Number node
     */
    Node<String> bitNot(Node<String> numberNode) {
        return node(Helper.BIT_NOT_NODE, numberNode);
    }

    /**
     * Get bitwise "AND"
     *
     * @param number1Node The node of number 1
     * @param number2Node The node of number 2
     */
    Node<String> bitAnd(Node<String> number1Node, Node<String> number2Node) {
        return node(Helper.BRACKET_L_NODE, number1Node, Helper.BIT_AND_NODE, number2Node, Helper.BRACKET_R_NODE);
    }

    /**
     * Get bitwise "OR"
     *
     * @param number1Node The node of number 1
     * @param number2Node The node of number 2
     */
    Node<String> bitOr(Node<String> number1Node, Node<String> number2Node) {
        return node(Helper.BRACKET_L_NODE, number1Node, Helper.BIT_OR_NODE, number2Node, Helper.BRACKET_R_NODE);
    }

    /**
     * Get bitwise "Exclusive or"
     *
     * @param number1Node The node of number 1
     * @param number2Node The node of number 2
     */
    Node<String> bitXor(Node<String> number1Node, Node<String> number2Node) {
        return node(Helper.BRACKET_L_NODE, number1Node, Helper.BIT_XOR_NODE, number2Node, Helper.BRACKET_R_NODE);
    }

    /**
     * Get left shift
     *
     * @param number1Node The node of number 1
     * @param number2Node The node of number 2
     */
    Node<String> shiftLeft(Node<String> number1Node, Node<String> number2Node) {
        return node(Helper.BRACKET_L_NODE, number1Node, Helper.INT4_NODE, Helper.SHIFT_LEFT_NODE, number2Node, Helper.INT4_NODE, Helper.BRACKET_R_NODE);
    }

    /**
     * Get shift right
     *
     * @param number1Node The node of number 1
     * @param number2Node The node of number 2
     */
    Node<String> shiftRight(Node<String> number1Node, Node<String> number2Node) {
        return node(Helper.BRACKET_L_NODE, number1Node, Helper.INT4_NODE, Helper.SHIFT_RIGHT_NODE, number2Node, Helper.INT4_NODE, Helper.BRACKET_R_NODE);
    }

    /**
     * Get string with left padding
     *
     * @param stringNode       String node
     * @param paddedLengthNode Padding length node
     * @param padStringNode    Filling string node
     */
    Node<String> lpad(Node<String> stringNode, Node<String> paddedLengthNode, Node<String> padStringNode) {
        return node(Helper.LPAD_NODE, stringNode, Helper.COMMA_NODE, paddedLengthNode, Helper.COMMA_NODE, padStringNode, Helper.BRACKET_R_NODE);
    }

    /**
     * Get string with right padding
     *
     * @param stringNode       String node
     * @param paddedLengthNode Padding length node
     * @param padStringNode    Filling string node
     */
    Node<String> rpad(Node<String> stringNode, Node<String> paddedLengthNode, Node<String> padStringNode) {
        return node(Helper.RPAD_NODE, stringNode, Helper.COMMA_NODE, paddedLengthNode, Helper.COMMA_NODE, padStringNode, Helper.BRACKET_R_NODE);
    }
}
