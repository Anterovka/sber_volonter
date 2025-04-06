package sbp.sbt.dataspacecore.configuration.utils;

import java.util.Locale;

public enum MultitenantMode {
    PREPARE, ALWAYS, NONE;

    public static MultitenantMode fromStringIgnoreCase(String valueStr) {
        return valueOf(valueStr.toUpperCase(Locale.ROOT));
    }
}
