package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.addNodeListToNodes;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.getString;
import static sbp.com.sbt.dataspace.feather.common.Node.node;

/**
 * Object based on string
 */
class StringBasedObject {

    Node<String> stringNode;
    Priority priority;

    /**
     * @param stringNode String node
     * @param priority   Priority
     */
    StringBasedObject(Node<String> stringNode, Priority priority) {
        this.stringNode = stringNode;
        this.priority = priority;
    }

    /**
     * Add string node
     *
     * @param nodes    Nodes
     * @param brackets Brackets
     */
    void addStringNode(List<Node<String>> nodes, boolean brackets) {
        if (brackets) {
            nodes.add(Helper.BRACKET_L_NODE);
        }
        nodes.add(stringNode);
        if (brackets) {
            nodes.add(Helper.BRACKET_R_NODE);
        }
    }

    /**
     * Add string node
     *
     * @param nodes    Nodes
     * @param priority Priority
     */
    void addStringNode(List<Node<String>> nodes, Priority priority) {
        addStringNode(nodes, this.priority.ordinal() >= priority.ordinal());
    }

    /**
     * Add string node
     *
     * @param nodes    Nodes
     * @param priority Priority
     */
    void addStringNode2(List<Node<String>> nodes, Priority priority) {
        addStringNode(nodes, this.priority.ordinal() > priority.ordinal());
    }

    /**
     * Get the node of the string for the system property
     *
     * @param systemPropertyNode System property node
     * @param parameterNodes     Parameter nodes
     */
    Node<String> getSystemPropertyStringNode(Node<String> systemPropertyNode, Node<String>... parameterNodes) {
        List<Node<String>> nodes = new ArrayList<>(4 + parameterNodes.length * 2);
        addStringNode(nodes, priority != Priority.VALUE);
        nodes.add(systemPropertyNode);
        if (parameterNodes.length != 0) {
            addNodeListToNodes(nodes, Helper.COMMA_NODE, Stream.of(parameterNodes));
            nodes.add(Helper.BRACKET_R_NODE);
        }
        return node(nodes);
    }

    /**
     * Get string node for property
     *
     * @param propertyName Property name
     */
    Node<String> getPropertyStringNode(String propertyName) {
        return node(stringNode, Helper.DOT_NODE, node(checkNotNull(propertyName, "Property name")));
    }

    /**
     * Get system primitive expression
     *
     * @param systemPropertyNode System property node
     * @param parameterNodes     Parameter nodes
     */
    PrimitiveExpressionImpl getSystemPrimitiveExpression(Node<String> systemPropertyNode, Node<String>... parameterNodes) {
        return new PrimitiveExpressionImpl(getSystemPropertyStringNode(systemPropertyNode, parameterNodes), Priority.VALUE);
    }

    /**
     * Get system primitive expression collection
     *
     * @param systemPropertyNode System property node
     * @param parameterNodes     Parameter nodes
     */
    PrimitiveExpressionsCollectionImpl getSystemPrimitiveExpressionsCollection(Node<String> systemPropertyNode, Node<String>... parameterNodes) {
        return new PrimitiveExpressionsCollectionImpl(getSystemPropertyStringNode(systemPropertyNode, parameterNodes), Priority.VALUE);
    }

    /**
     * Get system condition
     *
     * @param systemPropertyNode System property node
     * @param parameterNodes     Parameter nodes
     */
    ConditionImpl getSystemCondition(Node<String> systemPropertyNode, Node<String>... parameterNodes) {
        return new ConditionImpl(getSystemPropertyStringNode(systemPropertyNode, parameterNodes), Priority.VALUE);
    }

    @Override
    public String toString() {
        return getString(stringNode);
    }
}
