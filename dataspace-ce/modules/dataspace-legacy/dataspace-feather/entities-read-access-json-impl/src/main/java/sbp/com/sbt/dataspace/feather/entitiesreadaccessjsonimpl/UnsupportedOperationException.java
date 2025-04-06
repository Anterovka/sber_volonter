package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Unsupported operation
 */
public class UnsupportedOperationException extends FeatherException {

    /**
     * @param operationSignature Сигнатура операции
     */
    UnsupportedOperationException(String operationSignature) {
        super("Unsupported operation", param("Operation signature", operationSignature));
    }
}
