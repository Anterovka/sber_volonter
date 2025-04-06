package sbp.sbt.dataspacecore.utils.exceptions;

public class ValueNodeNotFound extends RuntimeException {
    public ValueNodeNotFound() {
        super();
    }

    public ValueNodeNotFound(String path) {
        super("Value not found on path "  + path);
    }

    public ValueNodeNotFound(String path, Throwable cause) {
        super("Value not found on path " + path, cause);
    }

    public ValueNodeNotFound(Throwable cause) {
        super(cause);
    }
}
