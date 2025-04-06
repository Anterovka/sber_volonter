package sbp.sbt.dataspacecore.utils;

/**
 * Data type
 */
public enum DataType {

    /**
     * Symbol
     */
    CHARACTER,
    /**
     * String
     */
    STRING,
    /**
     * Текст
     */
    TEXT,
    /**
     * Integer (1 byte)
     */
    BYTE,
    /**
     * Integer (2 bytes)
     */
    SHORT,
    /**
     * Integer (4 bytes)
     */
    INTEGER,
    /**
     * Integer (8 bytes)
     */
    LONG,
    /**
     * Floating point number (4 bytes)
     */
    FLOAT,
    /**
     * Floating point number (8 bytes)
     */
    DOUBLE,
    /**
     * A large decimal number
     */
    BIG_DECIMAL,
    /**
     * Date
     */
    DATE,
    /**
     * Date and time
     */
    DATETIME,
    /**
     * Date and time with offset
     */
    OFFSET_DATETIME,
    /**
     * Time
     */
    TIME,
    /**
     * Logical value
     */
    BOOLEAN,
    /**
     * Byte array
     */
    BYTE_ARRAY;
}
