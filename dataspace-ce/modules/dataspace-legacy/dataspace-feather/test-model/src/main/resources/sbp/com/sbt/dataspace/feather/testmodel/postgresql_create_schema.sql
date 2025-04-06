-- TestEntity {
CREATE TABLE F_TEST_ENTITY
(
    ID        VARCHAR(256) NOT NULL PRIMARY KEY,
    AGGREGATE VARCHAR(256),
    CODE      VARCHAR(4000),
    P1        VARCHAR(4000),
    P2        INT2,
    P3        INT2,
    P4        INT4,
    P5        BIGINT,
    P6        DOUBLE PRECISION,
    P7        TIMESTAMP(3),
    P8        BOOL,
    P9        BYTEA,
    P10       DECIMAL,
    P11       TEXT,
    P12       FLOAT4,
    P13       CHAR(1),
    P14       DATE,
    P15       TIMESTAMP(3) WITH TIME ZONE,
    R1        VARCHAR(256),
    G1_P1     VARCHAR(4000),
    G3_R1     VARCHAR(256)
);
CREATE INDEX I_F_TEST_ENTITY_AGGREGATE ON F_TEST_ENTITY (AGGREGATE);
CREATE INDEX I_F_TEST_ENTITY_R1 ON F_TEST_ENTITY (R1);
CREATE INDEX I_F_TEST_ENTITY_G3_R1 ON F_TEST_ENTITY (G3_R1);

CREATE TABLE F_TEST_ENTITY_PS1
(
    OWNER   VARCHAR(256),
    ELEMENT VARCHAR(4000),
    CONSTRAINT PK_F_TEST_ENTITY_PS1 PRIMARY KEY (OWNER, ELEMENT)
);
CREATE INDEX I_F_TEST_ENTITY_PS1_OWNER ON F_TEST_ENTITY_PS1 (OWNER);

CREATE TABLE F_TEST_ENTITY_PS2
(
    OWNER   VARCHAR(256),
    ELEMENT INT4,
    CONSTRAINT PK_F_TEST_ENTITY_PS2 PRIMARY KEY (OWNER, ELEMENT)
);
CREATE INDEX I_F_TEST_ENTITY_PS2_OWNER ON F_TEST_ENTITY_PS2 (OWNER);

CREATE TABLE F_TEST_ENTITY_PS3
(
    OWNER   VARCHAR(256),
    ELEMENT TIMESTAMP(3),
    CONSTRAINT PK_F_TEST_ENTITY_PS3 PRIMARY KEY (OWNER, ELEMENT)
);
CREATE INDEX I_F_TEST_ENTITY_PS3_OWNER ON F_TEST_ENTITY_PS3 (OWNER);

CREATE TABLE F_TEST_ENTITY_PS4
(
    OWNER   VARCHAR(256),
    ELEMENT BOOL,
    CONSTRAINT PK_F_TEST_ENTITY_PS4 PRIMARY KEY (OWNER, ELEMENT)
);
CREATE INDEX I_F_TEST_ENTITY_PS4_OWNER ON F_TEST_ENTITY_PS4 (OWNER);

CREATE TABLE F_TEST_ENTITY_RC1
(
    OWNER   VARCHAR(256),
    ELEMENT VARCHAR(256),
    CONSTRAINT PK_F_TEST_ENTITY_RC1 PRIMARY KEY (OWNER, ELEMENT)
);
CREATE INDEX I_F_TEST_ENTITY_RC1_OWNER ON F_TEST_ENTITY_RC1 (OWNER);
CREATE INDEX I_F_TEST_ENTITY_RC1_ELEMENT ON F_TEST_ENTITY_RC1 (ELEMENT);

CREATE TABLE F_TEST_ENTITY_SYSTEM_LOCKS
(
    AGGREGATE VARCHAR(256) NOT NULL PRIMARY KEY,
    VERSION   BIGINT
);
-- }

-- Entity {
CREATE TABLE F_ENTITY
(
    ID   VARCHAR(256) NOT NULL PRIMARY KEY,
    TYPE VARCHAR(4000),
    CODE VARCHAR(4000),
    NAME VARCHAR(4000)
);

CREATE TABLE F_ENTITY_ATTRIBUTES
(
    OWNER   VARCHAR(256),
    ELEMENT VARCHAR(4000),
    CONSTRAINT PK_F_ENTITY_ATTRIBUTES PRIMARY KEY (OWNER, ELEMENT)
);
CREATE INDEX I_F_ENTITY_ATTRIBUTES_OWNER ON F_ENTITY_ATTRIBUTES (OWNER);

CREATE TABLE F_ENTITY_SYSTEM_LOCKS
(
    AGGREGATE VARCHAR(256) NOT NULL PRIMARY KEY,
    VERSION   BIGINT
);
-- }

-- Request, RequestPlus {
CREATE TABLE F_REQUEST
(
    ID                   VARCHAR(256) NOT NULL PRIMARY KEY,
    TYPE                 VARCHAR(4000),
    AGGREGATE            VARCHAR(256),
    CODE                 VARCHAR(4000),
    CREATED_ENTITY       VARCHAR(256) UNIQUE,
    AGREEMENT            VARCHAR(256) UNIQUE,
    INITIATOR_FIRST_NAME VARCHAR(4000),
    INITIATOR_LAST_NAME  VARCHAR(4000),
    INITIATOR_AGE        INT4,
    INITIATOR_DOCUMENT   VARCHAR(256),
    DESCRIPTION          VARCHAR(4000),
    SPECIAL_CODE         VARCHAR(4000)
);
CREATE INDEX I_F_REQUEST_AGGREGATE ON F_REQUEST (AGGREGATE);
CREATE INDEX I_F_REQUEST_INITIATOR_DOCUMENT ON F_REQUEST (INITIATOR_DOCUMENT);
-- }

-- Parameter {
CREATE TABLE F_PARAMETER
(
    ID     VARCHAR(256) NOT NULL PRIMARY KEY,
    VALUE  VARCHAR(4000),
    ENTITY VARCHAR(256)
);
CREATE INDEX I_F_PARAMETER_ENTITY ON F_PARAMETER (ENTITY);
-- }

-- ActionParameter {
CREATE TABLE F_ACTION_PARAMETER
(
    ID            VARCHAR(256) NOT NULL PRIMARY KEY,
    EXECUTOR_NAME VARCHAR(4000)
);
-- }

-- ActionParameterSpecial {
CREATE TABLE F_ACTION_PARAMETER_SPECIAL
(
    ID            VARCHAR(256) NOT NULL PRIMARY KEY,
    SPECIAL_OFFER VARCHAR(4000)
);
-- }

-- Service {
CREATE TABLE F_SERVICE
(
    ID                    VARCHAR(256) NOT NULL PRIMARY KEY,
    MANAGER_PERSONAL_CODE BIGINT,
    START_ACTION          VARCHAR(256),
    INITIATOR_FIRST_NAME  VARCHAR(4000),
    INITIATOR_LAST_NAME   VARCHAR(4000),
    INITIATOR_AGE         INT4,
    INITIATOR_DOCUMENT    VARCHAR(256)
);
CREATE INDEX I_F_SERVICE_START_ACTION ON F_SERVICE (START_ACTION);
CREATE INDEX I_F_SERVICE_INITIATOR_DOCUMENT ON F_SERVICE (INITIATOR_DOCUMENT);
-- }

-- Operation {
CREATE TABLE F_OPERATION
(
    ID      VARCHAR(256) NOT NULL PRIMARY KEY,
    SERVICE VARCHAR(256)
);
CREATE INDEX I_F_OPERATION_SERVICE ON F_OPERATION (SERVICE);
-- }

-- OperationSpecial {
CREATE TABLE F_OPERATION_SPECIAL
(
    ID                   VARCHAR(256) NOT NULL PRIMARY KEY,
    SPECIAL_OFFER        VARCHAR(4000),
    PRODUCT              VARCHAR(256),
    INITIATOR_FIRST_NAME VARCHAR(4000),
    INITIATOR_LAST_NAME  VARCHAR(4000),
    INITIATOR_AGE        INT4,
    INITIATOR_DOCUMENT   VARCHAR(256)
);
CREATE INDEX I_F_OPERATION_SPECIAL_PRODUCT ON F_OPERATION_SPECIAL (PRODUCT);
CREATE INDEX I_F_OPERATION_SPECIAL_INITIATOR_DOCUMENT ON F_OPERATION_SPECIAL (INITIATOR_DOCUMENT);
-- }

-- OperationLimited {
CREATE TABLE F_OPERATION_LIMITED
(
    ID            VARCHAR(256) NOT NULL PRIMARY KEY,
    LIMITED_OFFER VARCHAR(4000),
    END_DATE      TIMESTAMP(3),
    PRODUCT       VARCHAR(256)
);
CREATE INDEX I_F_OPERATION_LIMITED_PRODUCT ON F_OPERATION_LIMITED (PRODUCT);
-- }

-- Action {
CREATE TABLE F_ACTION
(
    ID             VARCHAR(256) NOT NULL PRIMARY KEY,
    ALGORITHM_CODE BIGINT,
    OPERATION      VARCHAR(256)
);
CREATE INDEX I_F_ACTION_OPERATION ON F_ACTION (OPERATION);
-- }

-- ActionSpecial {
CREATE TABLE F_ACTION_SPECIAL
(
    ID            VARCHAR(256) NOT NULL PRIMARY KEY,
    SPECIAL_OFFER VARCHAR(4000)
);
-- }

-- Event {
CREATE TABLE F_EVENT
(
    ID     VARCHAR(256) NOT NULL PRIMARY KEY,
    AUTHOR VARCHAR(4000)
);
-- }

-- Product {
CREATE TABLE F_PRODUCT
(
    ID              VARCHAR(256) NOT NULL PRIMARY KEY,
    CREATOR_CODE    BIGINT,
    RELATED_PRODUCT VARCHAR(256),
    MAIN_DOCUMENT   VARCHAR(256)
);
CREATE INDEX I_F_PRODUCT_RELATED_PRODUCT ON F_PRODUCT (RELATED_PRODUCT);
CREATE INDEX I_F_PRODUCT_MAIN_DOCUMENT ON F_PRODUCT (MAIN_DOCUMENT);

CREATE TABLE F_PRODUCT_ALIASES
(
    OWNER   VARCHAR(256),
    ELEMENT VARCHAR(4000) NOT NULL,
    CONSTRAINT PK_F_PRODUCT_ALIASES PRIMARY KEY (OWNER, ELEMENT)
);
CREATE INDEX I_F_PRODUCT_ALIASES_OWNER ON F_PRODUCT_ALIASES (OWNER);

CREATE TABLE F_PRODUCT_RATES
(
    OWNER   VARCHAR(256),
    ELEMENT DOUBLE PRECISION NOT NULL,
    CONSTRAINT PK_F_PRODUCT_RATES PRIMARY KEY (OWNER, ELEMENT)
);
CREATE INDEX I_F_PRODUCT_RATES_OWNER ON F_PRODUCT_RATES (OWNER);

CREATE TABLE F_PRODUCT_SERVICES
(
    OWNER   VARCHAR(256),
    ELEMENT VARCHAR(256),
    CONSTRAINT PK_F_PRODUCT_SERVICES PRIMARY KEY (OWNER, ELEMENT)
);
CREATE INDEX I_F_PRODUCT_SERVICES_OWNER ON F_PRODUCT_SERVICES (OWNER);
CREATE INDEX I_F_PRODUCT_SERVICES_ELEMENT ON F_PRODUCT_SERVICES (ELEMENT);

CREATE TABLE F_PRODUCT_EVENTS
(
    OWNER   VARCHAR(256),
    ELEMENT VARCHAR(256) NOT NULL,
    CONSTRAINT PK_F_PRODUCT_EVENTS PRIMARY KEY (OWNER, ELEMENT)
);
CREATE INDEX I_F_PRODUCT_EVENTS_OWNER ON F_PRODUCT_EVENTS (OWNER);
CREATE INDEX I_F_PRODUCT_EVENTS_ELEMENT ON F_PRODUCT_EVENTS (ELEMENT);
-- }

-- ProductPlus {
CREATE TABLE F_PRODUCT_PLUS
(
    ID VARCHAR(256) NOT NULL PRIMARY KEY
);

CREATE TABLE F_PRODUCT_PLUS_AFFECTED_PRODUCTS
(
    OWNER   VARCHAR(256),
    ELEMENT VARCHAR(256) NOT NULL,
    CONSTRAINT PK_F_PRODUCT_PLUS_AFFECTED_PRODUCTS PRIMARY KEY (OWNER, ELEMENT)
);
CREATE INDEX I_F_PRODUCT_PLUS_AFFECTED_PRODUCTS_OWNER ON F_PRODUCT_PLUS_AFFECTED_PRODUCTS (OWNER);
CREATE INDEX I_F_PRODUCT_PLUS_AFFECTED_PRODUCTS_ELEMENT ON F_PRODUCT_PLUS_AFFECTED_PRODUCTS (ELEMENT);
-- }

-- ProductLimited {
CREATE TABLE F_PRODUCT_LIMITED
(
    ID            VARCHAR(256) NOT NULL PRIMARY KEY,
    LIMITED_OFFER VARCHAR(4000)
);
-- }

-- Document, Permission, Agreement, AgreementSpecial {
CREATE TABLE F_DOCUMENT
(
    ID                 VARCHAR(256) NOT NULL PRIMARY KEY,
    TYPE               VARCHAR(4000),
    CODE               VARCHAR(4000),
    STATUS             VARCHAR(4000),
    PRODUCT            VARCHAR(256),
    NUMBER_            BIGINT,
    PARTICIPANT        VARCHAR(4000),
    DOCUMENT           VARCHAR(256),
    SPECIAL_CONDITIONS VARCHAR(4000)
);
CREATE INDEX I_F_DOCUMENT_PRODUCT ON F_DOCUMENT (PRODUCT);
CREATE INDEX I_F_DOCUMENT_DOCUMENT ON F_DOCUMENT (DOCUMENT);
-- }

-- EntityA {
CREATE TABLE F_ENTITY_A
(
    ID     VARCHAR(256) NOT NULL PRIMARY KEY,
    TYPE   VARCHAR(4000),
    CODE   VARCHAR(256),
    REF_B  VARCHAR(256),
    REF2_B VARCHAR(256)
);
CREATE INDEX I_F_ENTITY_A_REF_B ON F_ENTITY_A (REF_B);
CREATE INDEX I_F_ENTITY_A_REF2_B ON F_ENTITY_A (REF2_B);
-- }

-- EntityAA {
CREATE TABLE F_ENTITY_AA
(
    ID      VARCHAR(256) NOT NULL PRIMARY KEY,
    TYPE    VARCHAR(4000),
    CODE_AA VARCHAR(256)
);
-- }

-- EntityAAA {
CREATE TABLE F_ENTITY_AAA
(
    ID       VARCHAR(256) NOT NULL PRIMARY KEY,
    TYPE     VARCHAR(4000),
    CODE_AAA VARCHAR(256),
    REF_E    VARCHAR(256)
);
CREATE INDEX I_F_ENTITY_AAA_REF_E ON F_ENTITY_AAA (REF_E);
-- }

-- EntityB {
CREATE TABLE F_ENTITY_B
(
    ID    VARCHAR(256) NOT NULL PRIMARY KEY,
    TYPE  VARCHAR(4000),
    CODE  VARCHAR(256),
    REF_C VARCHAR(256)
);
CREATE INDEX I_F_ENTITY_B_REF_C ON F_ENTITY_B (REF_C);
-- }

-- EntityC {
CREATE TABLE F_ENTITY_C
(
    ID    VARCHAR(256) NOT NULL PRIMARY KEY,
    TYPE  VARCHAR(4000),
    CODE  VARCHAR(256),
    REF_D VARCHAR(256)
);
CREATE INDEX I_F_ENTITY_C_REF_D ON F_ENTITY_C (REF_D);
-- }

-- EntityD {
CREATE TABLE F_ENTITY_D
(
    ID    VARCHAR(256) NOT NULL PRIMARY KEY,
    TYPE  VARCHAR(4000),
    CODE  VARCHAR(256),
    REF_E VARCHAR(256)
);
CREATE INDEX I_F_ENTITY_D_REF_E ON F_ENTITY_D (REF_E);
-- }

-- EntityE {
CREATE TABLE F_ENTITY_E
(
    ID   VARCHAR(256) NOT NULL PRIMARY KEY,
    TYPE VARCHAR(4000),
    CODE VARCHAR(256)
);
-- }
