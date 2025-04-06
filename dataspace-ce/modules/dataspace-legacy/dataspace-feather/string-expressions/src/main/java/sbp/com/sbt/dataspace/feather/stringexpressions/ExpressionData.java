package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.expressions.Entity;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.GroupDescription;

/**
 * These expressions
 */
class ExpressionData {

    ExpressionType type;
    Object data;
    EntityDescription entityDescription;
    GroupDescription groupDescription;

    /**
     * @param type Type of expression
     * @param data Data
     */
    ExpressionData(ExpressionType type, Object data) {
        this.type = type;
        this.data = data;
    }

    /**
     * @param entity            Entity
     * @param entityDescription The name of the entity
     */
    ExpressionData(Entity entity, EntityDescription entityDescription) {
        this(ExpressionType.ENTITY, entity);
        this.entityDescription = entityDescription;
    }


    @Override
    public String toString() {
        return type.description;
    }
}
