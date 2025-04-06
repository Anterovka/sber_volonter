package com.sbt.mg.jpa;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JpaConstants {
    private JpaConstants() {

    }

    public static final String SYSTEM_RELEASE_ALLOW = "MODEL_RELEASE";

    public static final String LAST_CHANGE_DATE_PROPERTY = "lastChangeDate";
    public static final String STATUS_HISTORY_PROPERTY_NAME = "statusHistory";
    public static final String HISTORY_BASE_POSTFIX = "History";
    public static final String HISTORY_OWNER_PROPERTY = "sysHistoryOwner";
    public static final String HISTORY_TIME_PROPERTY = "sysHistoryTime";
    public static final String HISTORY_STATE_PROPERTY = "sysState";
    public static final String HISTORY_NUMBER_PROPERTY = "sysHistNumber";
    public static final String LAST_HIST_VERSION_PROPERTY = "sysLastHistVersion";
    public static final String HIST_CHANGE_USER = "sysChangeUser";
    public static final String HIST_AGREGATE_ROOT = "aggregateRoot";

    /**
     * The name of the entity from which all "first" classes of the consumer model are inherited, with the exception of reference books
     */
    public static final String ENTITY_CLASS_NAME = "Entity";
    public static final String BASE_EVENT_NAME = "BaseEvent";
    public static final String BASE_MERGE_EVENT_NAME = "BaseMergeEvent";
    public static final String OBJECT_ID = "objectId";
    public static final String COMPOSITE_ID = "compositeId";
    public static final String OWNER_ID = "ownerId";

    public static final String OBJECT_ID_PREFIX = "sysIdPrefix";
    public static final String SYS_CLONE_ORIGIN = "sysCloneOrigin";

    public static final String ROOT_DICTIONARY_CLASS_NAME = "RootDictionary";
    public static final String ROOT_SECURITY_CLASS_NAME = "SysRootSecurity";

    public static final String DEFAULT_DICTIONARY_NAME_DIR = "dictionary";

    public static final String JPA_CLASS_PREFIX_NAME = "Jpa";

    public static final String PDM_MODEL_FILENAME = "pdm.xml";
    public static final String SUBSCRIPTIONS_FILENAME = "subscriptions.xml";
    public static final String SECURITY_CONFIG_DIR_NAME = "securityConfig";
    public static final String GRAPHQLPERMISSIONS_FILE_NAME = "graphql-permissions.json";
    public static final String JWKS_FILE_NAME = "jwks.json";
    public static final String PDM_BUILD_MODEL_FILENAME = "pdm-build.xml";
    public static final String MODEL_FILENAME = "model.xml";

    public static final String CHANGELOG_FILENAME = "changelog.xml";
    public static final String CHANGELOG_BUILD_FILENAME = "changelog-build.xml";
    public static final String SECURITY_CHANGELOG_FILENAME = "securityChangelog.xml";
    public static final String CCI_INDEX_INFO_FILENAME = "cciIndex.info";
    public static final String AGGREGATE_REFERENCE_VALIDATION_SQL_FILENAME = "agg-ref-validation.info";
    public static final String DB_INTEGRITY_VALIDATION_SQL_FILENAME = "schema-integrity-check.sql";
    public static final String ROLLBACK_CHANGELOG_FILENAME = "rollback-changelog.xml";
    public static final String CUSTOM_CHANGELOG_FILENAME = "custom-changelog.xml";
    public static final String CUSTOM_CHANGELOG = "custom-changelog";
    public static final String DBMS_PROPERTIES_FILENAME = "dbms-properties.xml";
    public static final String ROLLBACK_CHANGELOG_DIR = "changelogs";

    public static final String JPA_DISCRIMINATOR_NAME = "type";
    public static final String ID_NAME = "id";
    public static final String AGGREGATE_ROOT = "aggregateRoot";

    public static final String DEV_SNAPSHOT = "DEV-SNAPSHOT";

    public static final int MIN_LENGTH = 1;
    public static final int MAX_STRING_LENGTH = 4_000;
    public static final int MAX_UNICODE_STRING_LENGTH = 2_000;
    public static final int MAX_BIG_DECIMAL_LENGTH = 38; // oracle = 38, pg = 1000

    public static final int DEFAULT_MAX_DB_OBJECT_NAME_LENGTH = 63;
    public static final int MIN_VALUE_FOR_MAX_DB_OBJECT_NAME_LENGTH = 63;
    public static final int MAX_VALUE_FOR_MAX_DB_OBJECT_NAME_LENGTH = 254;

    public static final int MAX_VALUE_FOR_MAX_CLASS_NAME_LENGTH = 254;
    public static final int MIN_VALUE_FOR_MAX_CLASS_NAME_LENGTH = 40;
    public static final int MAX_VALUE_FOR_MAX_PROPERTY_NAME_LENGTH = 254;
    public static final int MIN_VALUE_FOR_MIN_CROPPED_CLASS_NAME_LENGTH = 3;
    public static final int MIN_VALUE_FOR_MIN_CROPPED_PROPERTY_NAME_LENGTH = 3;
    // minimum number of characters in the object name after truncation
    public static final int DEFAULT_MIN_CROPPED_CLASS_NAME_LENGTH = 15;
    public static final int DEFAULT_MIN_CROPPED_PROPERTY_NAME_LENGTH = 15;
    //The maximum length of the CCI index name. Requirement from the CCI service .
    public static final int MAX_CCI_NAME_LENGTH = 128;
    //The maximum length of the CCI index key. A requirement from the CCI service. A string, because the default value in the plugin.
    public static final String MAX_CCI_KEY_LENGTH = "100";

    public static final int MAX_DATE_LENGTH = 6;

    public static final int ORACLE_VALUE_RESTRICTION = 2000;

    /**
     * The maximum size of the file with reference data for splitting. 524,288 is 512KB
     * The text assumes that the data should squeeze into a vector. The maximum size of the vector is 1 MB. + headers - it's unknown how many there are.
     */
    public static final String DEFAULT_MAX_DICTIONARY_FILE_SIZE_BYTES = "524288";

    public static String userModelDir() {
        return "model";
    }

    public static String dictionaryPartsDir() {
        return "changelogFiles";
    }

    public static String userLocalModelDir() {
        return "localmodel";
    }

    public static String temporaryChangelogFilesDirectory() {
        return "_tempChangelogFiles";
    }
}
