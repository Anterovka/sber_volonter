package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * No reverse link description found for the links collection
 */
public class ReferencesCollectionBackReferenceDescriptionNotFoundException extends FeatherException {

    /**
     * @param propertyName Property name
     */
    ReferencesCollectionBackReferenceDescriptionNotFoundException(String propertyName) {
        super("No description of the backlink was found for the link collection", param("Property name", propertyName));
    }
}
