package com.sbt.parameters.plugin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImportSpecification {

    /**
     * Status model
     */
    private boolean status = true;
    /**
     * Merging/splitting (deduplication) and transfer between shards
     */
    private boolean cloudReplicationTasks = true;
    /**
     * CLOUD-RELOCATION
     */
    private boolean cloudRelocation = true;
    private boolean shedLock = true;
    /**
     * Versioning aggregates and replication in StandIn
     */
    private boolean sysLockModel = true;
    /**
     * Subscriptions
     */
    private boolean subscriptions = true;
    /**
     * Data integrity control in DB
     */
    private boolean integrityCheck = true;
    private boolean sysVersion = true;
    private boolean sysConfig = true;
    private boolean security = true;
    /**
     * Historization of attributes
     */
    private boolean history = true;
    /**
     * Интеграция с ARCH
     */
    private boolean sysInitTasks = true;
}
