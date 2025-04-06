package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.expressions.ConditionalGroup;

/**
 * Implementation of conditional group
 */
public class ConditionalGroupImpl extends StringBasedObject implements ConditionalGroup {

    /**
     * @param stringNode String node
     * @param priority   Priority
     */
    ConditionalGroupImpl(Node<String> stringNode, Priority priority) {
        super(stringNode, priority);
    }
}
