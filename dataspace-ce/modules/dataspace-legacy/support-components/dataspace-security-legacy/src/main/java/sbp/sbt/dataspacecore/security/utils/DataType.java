package sbp.sbt.dataspacecore.security.utils;

public enum DataType {
    JWT, JSON;

    public static DataType byString(String value) {
        if ("JWT".equalsIgnoreCase(value)) {
            return JWT;
        }
        if ("JSON".equalsIgnoreCase(value)) {
            return JSON;
        }

        throw new IllegalArgumentException("Unknown DataType value: " + value);
    }
}
