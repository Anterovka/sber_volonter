package sbp.com.sbt.dataspace.feather.expressionscommon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sbp.com.sbt.dataspace.feather.expressions.AliasedEntitySpecification;
import sbp.com.sbt.dataspace.feather.expressions.BackReferenceReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.BackReferenceReferencesCollectionSpecification;
import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollectionBackReferenceReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollectionReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesSpecification;
import sbp.com.sbt.dataspace.feather.expressions.EntityElementSpecification;
import sbp.com.sbt.dataspace.feather.expressions.GroupReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.GroupsCollectionReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressions.PrimitivesCollectionSpecification;
import sbp.com.sbt.dataspace.feather.expressions.ReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.ReferencesCollectionSpecification;
import sbp.com.sbt.dataspace.feather.expressions.RootSpecification;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Testing specifications")
public class SpecificationsTest {

    static final String ENTITY_TYPE = "Entity";
    static final String ALIAS = "alias";
    static final Condition CONDITION = new Condition() {
        @Override
        public Condition not() {
            return null;
        }

        @Override
        public Condition and(Condition condition1, Condition... conditions) {
            return null;
        }

        @Override
        public Condition or(Condition condition1, Condition... conditions) {
            return null;
        }

        @Override
        public PrimitiveExpression asBoolean() {
            return null;
        }
    };
    static final String PARAMETER_NAME = "parameter";
    static final String PARAMETER_VALUE = "Test";

    @DisplayName("Test of root entity specification")
    @Test
    public void rootSpecificationTest() {
        RootSpecification rootSpecification = new RootSpecificationImpl()
                .setType(ENTITY_TYPE);
        assertEquals(ENTITY_TYPE, rootSpecification.getType());
    }

    @DisplayName("Test specification of the collection entity")
    @Test
    public void entityElementSpecificationTest() {
        EntityElementSpecification entityElementSpecification = new EntityElementSpecificationImpl()
                .setType(ENTITY_TYPE);
        assertEquals(ENTITY_TYPE, entityElementSpecification.getType());
    }

    @DisplayName("Entity Specification Test with alias")
    @Test
    public void aliasedEntitySpecificationTest() {
        AliasedEntitySpecification aliasedEntitySpecification = new AliasedEntitySpecificationImpl()
                .setType(ENTITY_TYPE);
        assertEquals(ENTITY_TYPE, aliasedEntitySpecification.getType());
    }

    @DisplayName("Entity Specification Test")
    @Test
    public void entitiesSpecificationTest() {
        EntitiesSpecification entitiesSpecification = new EntitiesSpecificationImpl()
                .setType(ENTITY_TYPE)
                .setElementAlias(ALIAS)
                .setCondition(CONDITION)
                .setParameter(PARAMETER_NAME, PARAMETER_VALUE);
        assertEquals(ENTITY_TYPE, entitiesSpecification.getType());
        assertEquals(ALIAS, entitiesSpecification.getElementAlias());
        assertEquals(CONDITION, entitiesSpecification.getCondition());
        assertEquals(PARAMETER_VALUE, entitiesSpecification.getParameters().get(PARAMETER_NAME));
    }

    @DisplayName("Test of primitive collection specification")
    @Test
    public void primitivesCollectionSpecificationTest() {
        PrimitivesCollectionSpecification primitivesCollectionSpecification = new PrimitivesCollectionSpecificationImpl()
            .setCondition(CONDITION);
        assertEquals(CONDITION, primitivesCollectionSpecification.getCondition());
    }

    @DisplayName("Specification link test")
    @Test
    public void referenceSpecificationTest() {
        ReferenceSpecification referenceSpecification = new ReferenceSpecificationImpl()
                .setType(ENTITY_TYPE)
                .setAlias(ALIAS);
        assertEquals(ENTITY_TYPE, referenceSpecification.getType());
        assertEquals(ALIAS, referenceSpecification.getAlias());
    }

    @DisplayName("Specification test link with backlink")
    @Test
    public void backReferenceSpecificationTest() {
        BackReferenceReferenceSpecification backReferenceReferenceSpecification = new BackReferenceReferenceSpecificationImpl()
                .setType(ENTITY_TYPE)
                .setAlias(ALIAS);
        assertEquals(ENTITY_TYPE, backReferenceReferenceSpecification.getType());
        assertEquals(ALIAS, backReferenceReferenceSpecification.getAlias());
    }

    @DisplayName("Collection Link Specification Test")
    @Test
    public void referencesCollectionSpecificationTest() {
        ReferencesCollectionSpecification referencesCollectionSpecification = new ReferencesCollectionSpecificationImpl()
                .setType(ENTITY_TYPE)
                .setElementAlias(ALIAS)
                .setCondition(CONDITION);
        assertEquals(ENTITY_TYPE, referencesCollectionSpecification.getType());
        assertEquals(ALIAS, referencesCollectionSpecification.getElementAlias());
        assertEquals(CONDITION, referencesCollectionSpecification.getCondition());
    }

    @DisplayName("Test specification collection of links with backlink")
    @Test
    public void backReferenceReferencesCollectionSpecificationTest() {
        BackReferenceReferencesCollectionSpecification backReferenceReferencesCollectionSpecification = new BackReferenceReferencesCollectionSpecificationImpl()
                .setType(ENTITY_TYPE)
                .setElementAlias(ALIAS)
                .setCondition(CONDITION);
        assertEquals(ENTITY_TYPE, backReferenceReferencesCollectionSpecification.getType());
        assertEquals(ALIAS, backReferenceReferencesCollectionSpecification.getElementAlias());
        assertEquals(CONDITION, backReferenceReferencesCollectionSpecification.getCondition());
    }

    @DisplayName("Grouping link specification test")
    @Test
    public void groupReferenceSpecificationTest() {
        GroupReferenceSpecification reference = new GroupReferenceSpecificationImpl()
                .setType(ENTITY_TYPE);
        assertEquals(ENTITY_TYPE, reference.getType());
    }

    @DisplayName("Test of the collection entity reference specification")
    @Test
    public void entitiesCollectionReferenceSpecificationTest() {
        EntitiesCollectionReferenceSpecification referenceSpecification = new EntitiesCollectionReferenceSpecificationImpl()
                .setType(ENTITY_TYPE);
        assertEquals(ENTITY_TYPE, referenceSpecification.getType());
    }

    @DisplayName("Test specification link with backlink to entity collection")
    @Test
    public void entitiesCollectionBackReferenceSpecificationTest() {
        EntitiesCollectionBackReferenceReferenceSpecification backReferenceReferenceSpecification = new EntitiesCollectionBackReferenceReferenceSpecificationImpl()
                .setType(ENTITY_TYPE);
        assertEquals(ENTITY_TYPE, backReferenceReferenceSpecification.getType());
    }

    @DisplayName("Test specification link collection grouping")
    @Test
    public void groupsCollectionReferenceSpecificationTest() {
        GroupsCollectionReferenceSpecification referenceSpecification = new GroupsCollectionReferenceSpecificationImpl()
                .setType(ENTITY_TYPE);
        assertEquals(ENTITY_TYPE, referenceSpecification.getType());
    }
}
