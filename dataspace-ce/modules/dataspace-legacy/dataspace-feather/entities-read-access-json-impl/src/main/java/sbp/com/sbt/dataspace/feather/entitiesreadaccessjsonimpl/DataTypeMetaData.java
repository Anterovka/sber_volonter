package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.Function3;
import sbp.com.sbt.dataspace.feather.common.Node;

import java.sql.ResultSet;
import java.util.function.Function;

/**
 * Metadata of data type
 */
class DataTypeMetaData {

    Node<String> postgresqlCastNode;
    Function3<Object, SqlDialect, ResultSet, Integer> getPrimitiveValueFunction;
    Function<Object, Object> convertValueFunction;

    /**
     * @param postgresqlCastNode        The transformation node for the PostgreSQL type
     * @param getPrimitiveValueFunction Function for obtaining a primitive value
     * @param convertValueFunction      The value conversion function
     */
    DataTypeMetaData(Node<String> postgresqlCastNode, Function3<Object, SqlDialect, ResultSet, Integer> getPrimitiveValueFunction, Function<Object, Object> convertValueFunction) {
        this.postgresqlCastNode = postgresqlCastNode;
        this.getPrimitiveValueFunction = getPrimitiveValueFunction;
        this.convertValueFunction = convertValueFunction;
    }
}
