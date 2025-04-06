package sbp.com.sbt.dataspace.feather.expressiontyperesolver;

import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;

import java.util.HashMap;
import java.util.Map;

/**
 * Context
 */
class Context {

    EntityDescription elementEntityDescription;
    DataType elementDataType;
    Map<String, EntityDescription> aliasedEntityDescriptions = new HashMap<>();

    Context() {
    }

    /**
     * @param elementEntityDescription Description of the collection entity element
     */
    Context(EntityDescription elementEntityDescription) {
        this.elementEntityDescription = elementEntityDescription;
    }

    /**
     * @param collectionElementDataType The type of the collection element
     */
    Context(DataType elementDataType) {
        this.elementDataType = elementDataType;
    }

    /**
     * With entities described under the alias
     *
     * @return Current context
     */
    Context withAliasedEntityDescriptions(Map<String, EntityDescription> aliasedEntityDescriptions) {
        this.aliasedEntityDescriptions = aliasedEntityDescriptions;
        return this;
    }
}
