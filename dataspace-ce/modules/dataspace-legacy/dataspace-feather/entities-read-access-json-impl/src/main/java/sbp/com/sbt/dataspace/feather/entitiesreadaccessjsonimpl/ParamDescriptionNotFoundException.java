package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
* The parameter description is not found
 */
public class ParamDescriptionNotFoundException extends FeatherException {

    /**
* @param entityType Entity type
* @param paramName  Parameter name
     */
    ParamDescriptionNotFoundException(String entityType, String paramName) {
super("Description of parameter not found", param("Entity type", entityType), param("Parameter name", paramName));
    }
}
