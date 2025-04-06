package sbp.com.sbt.dataspace.feather.modeldescription;

import sbp.com.sbt.dataspace.feather.common.MetaDataManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;

/**
 * Data type
 */
public enum DataType implements ObjectWithMetaDataManager {

    /**
     * Symbol
     */
    CHARACTER(Character.class),
    /**
     * String
     */
    STRING(String.class),
    /**
     * Text
     */
    TEXT(String.class),
    /**
     * Integer (1 byte)
     */
    BYTE(Byte.class),
    /**
     * Integer (2 bytes)
     */
    SHORT(Short.class),
    /**
     * Integer (4 bytes)
     */
    INTEGER(Integer.class),
    /**
     * Integer (8 bytes)
     */
    LONG(Long.class),
    /**
     * Floating point number (4 bytes)
     */
    FLOAT(Float.class),
    /**
     * Floating point number (8 bytes)
     */
    DOUBLE(Double.class),
    /**
     * A large decimal number
     */
    BIG_DECIMAL(BigDecimal.class),
    /**
     * Date
     */
    DATE(LocalDate.class),
    /**
     * Date and time
     */
    DATETIME(LocalDateTime.class),
    /**
     * Date and time with offset
     */
    OFFSET_DATETIME(OffsetDateTime.class),
    /**
     * Time
     */
    TIME(LocalTime.class),
    /**
     * Logical value
     */
    BOOLEAN(Boolean.class),
    /**
     * Byte array
     */
    BYTE_ARRAY(byte[].class);

    Class<Object> class0;
    MetaDataManager metaDataManager = new MetaDataManager();

    /**
     * @param class1 Class
     */
    DataType(Class<?> class0) {
        this.class0 = (Class<Object>) class0;
    }

    /**
     * Get class
     */
    // NotNull
    public Class<Object> getClass0() {
        return class0;
    }

    @Override
    public MetaDataManager getMetaDataManager() {
        return metaDataManager;
    }
}
