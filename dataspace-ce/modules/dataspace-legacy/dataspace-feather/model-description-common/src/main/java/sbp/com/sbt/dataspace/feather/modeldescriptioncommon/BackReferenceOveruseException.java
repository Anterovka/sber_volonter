package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

/**
 * Abuse of backreference
 */
public class BackReferenceOveruseException extends FeatherException {

    BackReferenceOveruseException() {
        super("Abuse of back reference", "A link cannot be both reverse and for both a link collection simultaneously");
    }
}
