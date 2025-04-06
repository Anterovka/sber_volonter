package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

import java.util.Collections;
import java.util.Map;

/**
 * Context of expression
 */
class ExpressionContext {

    static final ExpressionContext EMPTY = new ExpressionContext();

    Node<String> collectionElementColumnNode;
    DataType collectionElementType;
    SqlQueryProcessor collectionElementSqlQueryProcessor;
    Map<String, AliasedEntityData> aliasedEntitiesData;

    ExpressionContext() {
        aliasedEntitiesData = Collections.emptyMap();
    }

    /**
     * @param collectionElementColumnNode The node of the column with the collection element
     * @param collectionElementType       The type of collection element
     */
    ExpressionContext(Node<String> collectionElementColumnNode, DataType collectionElementType) {
        this.collectionElementColumnNode = collectionElementColumnNode;
        this.collectionElementType = collectionElementType;
    }

    /**
     * @param collectionElementSqlQueryProcessor The processor of the SQL query of the collection element
     */
    ExpressionContext(SqlQueryProcessor collectionElementSqlQueryProcessor) {
        this.collectionElementSqlQueryProcessor = collectionElementSqlQueryProcessor;
    }
}
