package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import java.util.Set;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Unexpected sorting order
 */
public class UnexpectedSortOrderException extends FeatherException {

    /**
     * @param expectedSortOrders Expected sorting orders
     * @param sortOrderString    Sort order
     */
    UnexpectedSortOrderException(Set<String> expectedSortOrders, String sortOrderString) {
        super("Unexpected sorting order", param("Expected sorting orders", expectedSortOrders), param("Sorting order", sortOrderString));
    }
}
