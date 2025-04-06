package sbp.com.sbt.dataspace.feather.common;

/**
 * Received a null value
 */
public final class NullValueException extends FeatherException {

    /**
     * @param description Description
     */
    public NullValueException(String description) {
        super("Received null value", description);
    }
}
