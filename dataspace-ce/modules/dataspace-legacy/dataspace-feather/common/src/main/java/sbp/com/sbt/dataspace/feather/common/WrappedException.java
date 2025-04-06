package sbp.com.sbt.dataspace.feather.common;

/**
 * Wrapped exception
 */
public final class WrappedException extends FeatherException {

    /**
* @param throwable Exception
     */
    public WrappedException(Throwable throwable) {
        super(throwable, "Wrapped exception");
    }
}
