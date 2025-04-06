package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;

/**
 * Table data
 */
class TableData {

    EntityDescription entityDescription;
    Node<String> aliasNode;
    Node<String> tableNode;
    Node<String> idColumnNode;
    boolean added;
}
