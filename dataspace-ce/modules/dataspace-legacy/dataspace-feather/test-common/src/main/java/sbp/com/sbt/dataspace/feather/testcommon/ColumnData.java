package sbp.com.sbt.dataspace.feather.testcommon;

/**
 * The data of the column
 */
class ColumnData {

    String parameterName;
    Object value;

    /**
     * @param parameterName Parameter name
     * @param value         Value
     */
    ColumnData(String parameterName, Object value) {
        this.parameterName = parameterName;
        this.value = value;
    }
}
