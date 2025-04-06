package sbp.sbt.model.config.snowflake.core;

public interface StringIdGenerator {
    /**
     * Get the next value of the generator.
     *
     * @return The next value of the counter.
     */
    String getNextValue();
}
