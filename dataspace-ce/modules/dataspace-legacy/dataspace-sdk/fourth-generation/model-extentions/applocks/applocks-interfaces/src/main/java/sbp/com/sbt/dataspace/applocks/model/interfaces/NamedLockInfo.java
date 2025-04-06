package sbp.com.sbt.dataspace.applocks.model.interfaces;

/**
 * Interface for named (virtual) application locks.
 */
//On 2023-09-04, it is not used in PROM - the functionality is not routed to the API in ds-core.
//Implementation requires a separate entity (table) - locks are searched by name (SyalName).
// if the entity is not found - it is created.
// From the algorithm, it is required to have a multi-aggregate transaction (since one table cannot be linked to different types of aggregates).
//Also, there is a need for introducing a system entity that implements this interface, or maybe even more than one.
//In general, the functionality seems underdeveloped, underthought-out.
//The idea is interesting, but it requires further thought on the architecture. It should be noted that vectors for different aggregates are indirectly in SI.
//As an alternative, the consumer can create his own nested entity or set of entities within the aggregate and block them.
// The difference from the proposed option above is that here the search/creation/update of data can be performed in one call.
//find - create - update.
public interface NamedLockInfo extends LockInfo {

    String getSyalName();

    void setSyalName(String syalName);

    String getAffinityRootId();

    void setAffinityRootId(String affinityRootId);
}
