package sbp.com.sbt.dataspace.feather.common;

/**
 * Unexpected exception
 */
public final class UnexpectedException extends FeatherException {

    public UnexpectedException() {
        super("Unexpected exception");
    }

    /**
     * @param throwable Exception
     */
    public UnexpectedException(Throwable throwable) {
        super(throwable, "Unexpected exception");
    }
}
