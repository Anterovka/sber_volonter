package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The required setting is not set
 */
public class RequiredSettingNotSetException extends FeatherException {

    /**
     * @param setting The setting
     */
    RequiredSettingNotSetException(String setting) {
        super("The required setting is not set", param("Setting", setting));
    }
}
