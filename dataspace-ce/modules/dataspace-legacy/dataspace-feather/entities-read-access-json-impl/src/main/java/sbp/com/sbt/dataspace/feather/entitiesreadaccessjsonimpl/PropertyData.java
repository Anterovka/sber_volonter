package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import java.util.Objects;

/**
 * These properties
 */
class PropertyData {

    String propertyName;
    String alias;

    /**
     * @param propertyName Property name
     * @param alias        Alias
     */
    PropertyData(String propertyName, String alias) {
        this.alias = alias;
        this.propertyName = propertyName;
    }

    /**
     * Check
     *
     * @param propertyData Property data
     */
    void check(PropertyData propertyData) {
        if (propertyName == null || propertyData.propertyName == null || !(propertyName.equals(propertyData.propertyName) && Objects.equals(alias, propertyData.alias))) {
            throw new PropertiesTypeConflictFoundException(this, propertyData);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (propertyName == null) {
            stringBuilder.append("Computable expression");
        } else {
            stringBuilder.append("Property '").append(propertyName).append('\'');
        }
        if (alias != null) {
            stringBuilder.append(" under the pseudonym '")
                .append(alias)
                .append('\'');
        }
        return stringBuilder.toString();
    }
}
