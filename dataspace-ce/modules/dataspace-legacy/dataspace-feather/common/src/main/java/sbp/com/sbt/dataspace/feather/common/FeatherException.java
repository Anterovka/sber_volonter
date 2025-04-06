package sbp.com.sbt.dataspace.feather.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.getFullDescription;

/**
 * Exception of the library Feather
 */
public class FeatherException extends RuntimeException {

    /**
     * @param description Description
     * @param context     Контекст
     */
    protected FeatherException(String description, String... context) {
        super(getExceptionMessage(description, context));
    }

    /**
     * @param throwable   Exception
     * @param description Description
     * @param context     Контекст
     */
    protected FeatherException(Throwable throwable, String description, String... context) {
        super(getExceptionMessage(description, context), throwable);
    }

    /**
     * Get exception message
     *
     * @param description Description
     * @param context     Контекст
     */
    static String getExceptionMessage(String description, String... context) {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) + ": " + getFullDescription(description, context);
    }
}
