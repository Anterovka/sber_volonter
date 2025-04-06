package sbp.com.sbt.semver.exceptions;

public class SemverParseException extends RuntimeException {

    public SemverParseException(String version) {
        super(String.format("Error when parsing version %s according to Semver rules. The rules can be found at https://semver.org/", version));
    }
}
