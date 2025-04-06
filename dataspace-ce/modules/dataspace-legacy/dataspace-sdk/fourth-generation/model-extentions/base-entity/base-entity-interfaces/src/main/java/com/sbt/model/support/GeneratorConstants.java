package com.sbt.model.support;

import java.util.Arrays;

public final class GeneratorConstants {

    public static final String GENERATOR_REQUIRED_VALUE_PARAMETER_NAME = "required-external-value";
    public static final String GENERATOR_KIND_PARAMETER_NAME = "kind";

    public enum Kind {
        SNOWFLAKE("snowflake"),
        UUID_V4("uuid_v4");

        private final String parameterValue;

        Kind(String parameterValue) {
            this.parameterValue = parameterValue;
        }

        public String getParameterValue() {
            return parameterValue;
        }

        public static Kind byParameterValue(String parameterValue) {

            return Arrays.stream(values())
                    .filter(kind -> kind.getParameterValue().equals(parameterValue))
                    .findFirst()
                    .orElse(null);

        }

    }

    private GeneratorConstants() {
        /* no-ops */
    }
}
