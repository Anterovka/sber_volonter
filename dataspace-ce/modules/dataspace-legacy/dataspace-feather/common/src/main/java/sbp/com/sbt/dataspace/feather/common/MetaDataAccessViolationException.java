package sbp.com.sbt.dataspace.feather.common;

/**
 * Error accessing metadata on record
 */
public class MetaDataAccessViolationException extends FeatherException {

    MetaDataAccessViolationException() {
        super("Error accessing metadata for recording");
    }
}
