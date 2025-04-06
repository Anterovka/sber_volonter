package sbp.com.sbt.dataspace.utils.exceptions;

public class ClassPathUtilsException extends RuntimeException {
    public ClassPathUtilsException() {
        super();
    }

    public ClassPathUtilsException(String message) {
        super(message);
    }

    public ClassPathUtilsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassPathUtilsException(Throwable cause) {
        super(cause);
    }
}
