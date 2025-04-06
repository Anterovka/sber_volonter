package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.expressions.AliasedEntitySpecification;
import sbp.com.sbt.dataspace.feather.expressions.ConditionalGroup;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollection;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesSpecification;
import sbp.com.sbt.dataspace.feather.expressions.Entity;
import sbp.com.sbt.dataspace.feather.expressions.EntityElementSpecification;
import sbp.com.sbt.dataspace.feather.expressions.ExpressionsProcessor;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionsCollection;
import sbp.com.sbt.dataspace.feather.expressions.RootSpecification;
import sbp.com.sbt.dataspace.feather.expressionscommon.AliasedEntitySpecificationImpl;
import sbp.com.sbt.dataspace.feather.expressionscommon.EntitiesSpecificationImpl;
import sbp.com.sbt.dataspace.feather.expressionscommon.EntityElementSpecificationImpl;
import sbp.com.sbt.dataspace.feather.expressionscommon.RootSpecificationImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.addNodeListToNodes;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;
import static sbp.com.sbt.dataspace.feather.common.Node.node;
import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.checkImpl;
import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.getArrayNode;
import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.getExpression;
import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.getExpression4;
import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.getPrimitiveValueNode;

/**
 * Expression handler based on string
 */
class StringExpressionsProcessor implements ExpressionsProcessor {

    /**
     * Get entity with alias
     *
     * @param alias Alias
     */
    Node<String> getAliasedEntity(String alias) {
        return node(Helper.AT_NODE, node(alias));
    }

    @Override
    public Entity root(Consumer<RootSpecification> specificationCode) {
        return getExpression(EntityImpl::new, Helper.ROOT_NODE, RootSpecificationImpl::new, specificationCode);
    }

    @Override
    public Entity root() {
        return new EntityImpl(Helper.ROOT_NODE, Priority.VALUE);
    }

    @Override
    public PrimitiveExpression elemPE() {
        return new PrimitiveExpressionImpl(Helper.IT_NODE, Priority.VALUE);
    }

    @Override
    public Entity elemE(Consumer<EntityElementSpecification> specificationCode) {
        return getExpression(EntityImpl::new, Helper.IT_NODE, EntityElementSpecificationImpl::new, specificationCode);
    }

    @Override
    public Entity elemE() {
        return new EntityImpl(Helper.IT_NODE, Priority.VALUE);
    }

    @Override
    public Entity aliasedEntity(String alias, Consumer<AliasedEntitySpecification> specificationCode) {
        return getExpression(EntityImpl::new, getAliasedEntity(alias), AliasedEntitySpecificationImpl::new, specificationCode);
    }

    @Override
    public Entity aliasedEntity(String alias) {
        return new EntityImpl(getAliasedEntity(alias), Priority.VALUE);
    }

    @Override
    public PrimitiveExpression prim(Object primitiveValue) {
        return new PrimitiveExpressionImpl(getPrimitiveValueNode(primitiveValue, false, false), Priority.VALUE);
    }

    @Override
    public PrimitiveExpression now() {
        return new PrimitiveExpressionImpl(Helper.NOW, Priority.VALUE);
    }

    @Override
    public PrimitiveExpression coalesce(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        List<Node<String>> nodes = new ArrayList<>(3 + primitiveExpressions.length * 2);
        nodes.add(Helper.COALESCE_NODE);
        addNodeListToNodes(nodes, Helper.COMMA_NODE, Stream.concat(Stream.of(primitiveExpression1), Arrays.stream(primitiveExpressions)).map(primitiveExpression -> checkImpl(primitiveExpression).stringNode));
        nodes.add(Helper.BRACKET_R_NODE);
        return new PrimitiveExpressionImpl(node(nodes), Priority.VALUE);
    }

    @Override
    public EntitiesCollection entities(Consumer<EntitiesSpecification> specificationCode) {
        return getExpression4(EntitiesCollectionImpl::new, Helper.ENTITIES_NODE, EntitiesSpecificationImpl::new, specificationCode);
    }

    @Override
    public PrimitiveExpression rawPE(Object raw) {
        checkNotNull(raw, "RAW-объект");
        if (raw instanceof String) {
            return new PrimitiveExpressionImpl(node((String) raw), Priority.VALUE);
        } else {
            throw new UnexpectedRawClassException(raw.getClass());
        }
    }

    @Override
    public ConditionalGroup any(PrimitiveExpressionsCollection primitiveExpressionsCollection) {
        return new ConditionalGroupImpl(node(Helper.ANY_NODE, checkImpl(primitiveExpressionsCollection).stringNode, Helper.BRACKET_R_NODE), Priority.VALUE);
    }

    @Override
    public ConditionalGroup any(Object[] array) {
        return new ConditionalGroupImpl(node(Helper.ANY_NODE, getArrayNode(array), Helper.BRACKET_R_NODE), Priority.VALUE);
    }

    @Override
    public ConditionalGroup all(PrimitiveExpressionsCollection primitiveExpressionsCollection) {
        return new ConditionalGroupImpl(node(Helper.ALL_NODE, checkImpl(primitiveExpressionsCollection).stringNode, Helper.BRACKET_R_NODE), Priority.VALUE);
    }

    @Override
    public ConditionalGroup all(Object[] array) {
        return new ConditionalGroupImpl(node(Helper.ALL_NODE, getArrayNode(array), Helper.BRACKET_R_NODE), Priority.VALUE);
    }
}
