package com.sbt.liquibase.helper;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sbt.mg.utils.LiquibasePropertyUtils.replaceEscapeSymbols;

public final class Helper {

    private static final Map<String, String> REMARKS_WORDS;

    private Helper() {
        super();
    }

    static {
        Map<String, String> temp = new HashMap<>();

        List<String> strings = com.sbt.mg.Helper.getAllLines(Helper.class.getResourceAsStream("/templates/changelog/remarks.changelog.template"));
        strings.forEach(s -> {
            int delim = s.indexOf(':');

            temp.put(s.substring(0, delim), s.substring(delim + 1));
        });
        REMARKS_WORDS = Collections.unmodifiableMap(temp);
    }

    public static String createRemarks(XmlModelClassProperty modelClassProperty, boolean isDeprecated) {
        return createRemarks(modelClassProperty.getLabel(), modelClassProperty.getName(), isDeprecated);
    }

    public static String createRemarks(XmlModelClass modelClass, boolean isDeprecated) {
        return createRemarks(modelClass.getLabel(), modelClass.getName(), isDeprecated);
    }

    public static String createRemarks(String label, String name, boolean isDeprecated) {
        return REMARKS_WORDS.get("remarks1")
                .replace("${isDeleted}", isDeprecated ? "@Deprecated " : "")
                .replace("${label}", replaceEscapeSymbols(label))
                .replace("${name}", name);
    }
}
