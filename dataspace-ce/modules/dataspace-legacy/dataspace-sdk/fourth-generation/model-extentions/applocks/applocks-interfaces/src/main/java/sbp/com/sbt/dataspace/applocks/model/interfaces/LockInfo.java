package sbp.com.sbt.dataspace.applocks.model.interfaces;

import java.time.OffsetDateTime;
import java.util.Date;

/**
 * Interface of applied blocking.
 * The essence that supports this interface provides functionality for its application-level locking.
 */
public interface LockInfo {

    /** Duration of blocking in ms */
    Long getSyalTimeout();

    void setSyalTimeout(Long syalTimeout);

    @Deprecated
    /** The flag of the action for the application lock.
     * Is not the final status of application locking.
     * For status calculation, an additional analysis is required to ensure that the block has not expired over time.
     * (syalChangeDate + syalTimeout > now()).
     * In the new approach, it is not used, replaced with syalUnlockTime */
    Boolean getSyalActive();

    @Deprecated
    void setSyalActive(Boolean syalActive);

    /**
     * The token generated when setting or extending an application lock.
     * Required for lifting/extending application lock.
     */
    String getSyalToken();

    void setSyalToken(String syalToken);

    @Deprecated
    /** Time of changing application lock (time of installation, extension, removal).
     * In the new approach, it is not used, replaced with syalUnlockTime */
    Date getSyalChangeDate();

    @Deprecated
    void setSyalChangeDate(Date syalChangeDate);

    //The reason for installation, extension, or removal of an application lock
    String getSyalReason();

    void setSyalReason(String syalReason);

    /**Time (with time zone) until which the application lock is valid.
    In the new approach, if null - the lock is released.*/
    OffsetDateTime getSyalUnlockTime();

    void setSyalUnlockTime(OffsetDateTime syalUnlockTime);
}
