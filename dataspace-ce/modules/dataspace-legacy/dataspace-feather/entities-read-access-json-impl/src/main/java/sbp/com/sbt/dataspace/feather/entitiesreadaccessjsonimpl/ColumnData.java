package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

/**
 * The data of the column
 */
class ColumnData {

    SqlQueryProcessor sqlQueryProcessor;
    DataType type;
    Node<String> columnNode;
    int columnIndex;
    boolean inherit;
    boolean refId = false;

    /**
     * @param sqlQueryProcessor The SQL query processor
     * @param type              Тип
     * @param columnNode        The column node
     */
    ColumnData(SqlQueryProcessor sqlQueryProcessor, DataType type, Node<String> columnNode) {
        this.sqlQueryProcessor = sqlQueryProcessor;
        this.type = type;
        this.columnNode = columnNode;
    }
}
