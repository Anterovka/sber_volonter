package sbp.com.sbt.dataspace.utils;

public class MsgUtils {
    public static final String WRONG_AGGVERSION_REQUEST_ERROR_MESSAGE = "You cannot request the aggregate version for nested elements except for external links." +
        " It is necessary to move the call of the function setNeedAggregateVersion to the root element of the request or the first object of the external reference." +
        " Version is uniform for all objects within the aggregate.";

    private MsgUtils() {
    }


}
