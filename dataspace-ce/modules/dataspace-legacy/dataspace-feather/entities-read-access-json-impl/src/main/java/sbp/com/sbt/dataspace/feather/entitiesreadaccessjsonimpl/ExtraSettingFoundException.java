package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Extra setting detected
 */
public class ExtraSettingFoundException extends FeatherException {

    /**
     * @param setting The setting
     */
    ExtraSettingFoundException(String setting) {
        super("Additional setting detected", param("Setting", setting));
    }
}
