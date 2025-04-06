package sbp.com.sbt.dataspace.feather.testcommon;

/**
 * Test data
 */
public class TestData {

    String description;
    String name;
    String type;

    /**
     * @param description Description
     * @param name        Name
     * @param type        Тип
     */
    TestData(String description, String name, String type) {
        this.description = description;
        this.name = name;
        this.type = type;
    }

    /**
     * Get description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get type
     */
    public String getType() {
        return type;
    }

    /**
     * Get test data
     *
     * @param description Description
     * @param name        Name
     * @param type        Тип
     */
    public static TestData testData(String description, String name, String type) {
        return new TestData(description, name, type);
    }

    /**
     * Get test data
     *
     * @param description Description
     * @param name        Name
     */
    public static TestData testData(String description, String name) {
        return new TestData(description, name, null);
    }
}
