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

create table T_TABLE_A
(
  C_ID               varchar(256) not null primary key,
  C_CHAR             char(1),
  C_STRING           varchar(4000),
  C_TEXT             text,
  C_BYTE             int2,
  C_SHORT            int2,
  C_INT              int4,
  C_LONG             bigint,
  C_FLOAT            float4,
  C_DOUBLE           double precision,
  C_BIG_DECIMAL      decimal,
  C_LOCAL_DATE       date,
  C_LOCAL_TIME       time(6),
  C_LOCAL_DATE_TIME  timestamp(6),
  C_OFFSET_DATE_TIME timestamp(6) with time zone,
  C_BOOLEAN          bool,
  C_BYTE_ARRAY       bytea
);

CREATE TABLE lc_testentity_pbigdecimalcollection (
	testentity_id varchar(128) NOT NULL,
	pbigdecimalcollection numeric NOT NULL,
	CONSTRAINT lc_testentity_pbigdecimalcollection_pkey PRIMARY KEY (testentity_id, pbigdecimalcollection)
);
CREATE TABLE lc_testentity_pbytecollection (
	testentity_id varchar(128) NOT NULL,
	pbytecollection int2 NOT NULL,
	CONSTRAINT lc_testentity_pbytecollection_pkey PRIMARY KEY (testentity_id, pbytecollection)
);
CREATE TABLE lc_testentity_pcharcollection (
	testentity_id varchar(128) NOT NULL,
	pcharcollection bpchar(1) NOT NULL,
	CONSTRAINT lc_testentity_pcharcollection_pkey PRIMARY KEY (testentity_id, pcharcollection)
);
CREATE TABLE lc_testentity_pdatecollection (
	testentity_id varchar(128) NOT NULL,
	pdatecollection timestamp(3) NOT NULL,
	CONSTRAINT lc_testentity_pdatecollection_pkey PRIMARY KEY (testentity_id, pdatecollection)
);
CREATE TABLE lc_testentity_pdoublecollection (
	testentity_id varchar(128) NOT NULL,
	pdoublecollection float8 NOT NULL,
	CONSTRAINT lc_testentity_pdoublecollection_pkey PRIMARY KEY (testentity_id, pdoublecollection)
);
CREATE TABLE lc_testentity_pfloatcollection (
	testentity_id varchar(128) NOT NULL,
	pfloatcollection float4 NOT NULL,
	CONSTRAINT lc_testentity_pfloatcollection_pkey PRIMARY KEY (testentity_id, pfloatcollection)
);
CREATE TABLE lc_testentity_pintegercollection (
	testentity_id varchar(128) NOT NULL,
	pintegercollection int4 NOT NULL,
	CONSTRAINT lc_testentity_pintegercollection_pkey PRIMARY KEY (testentity_id, pintegercollection)
);
CREATE TABLE lc_testentity_plocaldatecollection (
	testentity_id varchar(128) NOT NULL,
	plocaldatecollection date NOT NULL,
	CONSTRAINT lc_testentity_plocaldatecollection_pkey PRIMARY KEY (testentity_id, plocaldatecollection)
);
CREATE TABLE lc_testentity_plocaldatetimecollection (
	testentity_id varchar(128) NOT NULL,
	plocaldatetimecollection timestamp(6) NOT NULL,
	CONSTRAINT lc_testentity_plocaldatetimecollection_pkey PRIMARY KEY (testentity_id, plocaldatetimecollection)
);
CREATE TABLE lc_testentity_plongcollection (
	testentity_id varchar(128) NOT NULL,
	plongcollection int8 NOT NULL,
	CONSTRAINT lc_testentity_plongcollection_pkey PRIMARY KEY (testentity_id, plongcollection)
);
CREATE TABLE lc_testentity_poffsetdatetimecollection (
	testentity_id varchar(128) NOT NULL,
	poffsetdatetimecollection timestamptz(6) NOT NULL,
	CONSTRAINT lc_testentity_poffsetdatetimecollection_pkey PRIMARY KEY (testentity_id, poffsetdatetimecollection)
);
CREATE TABLE lc_testentity_pshortcollection (
	testentity_id varchar(128) NOT NULL,
	pshortcollection int2 NOT NULL,
	CONSTRAINT lc_testentity_pshortcollection_pkey PRIMARY KEY (testentity_id, pshortcollection)
);
CREATE TABLE lc_testentity_pstringcollection (
	testentity_id varchar(128) NOT NULL,
	pstringcollection varchar(4000) NOT NULL,
	CONSTRAINT lc_testentity_pstringcollection_pkey PRIMARY KEY (testentity_id, pstringcollection)
);
CREATE TABLE product (
	id varchar(124) NOT NULL,
	code varchar(124) NULL,
	owner_first_name varchar(256) NULL,
	owner_last_name varchar(256) NULL,
	product_owner_passport_serial varchar(256) NULL,
	product_owner_passport_number varchar(256) NULL,
	product_owner_passport_signature_private varchar(256) NULL,
	product_owner_passport_signature_public varchar(256) NULL,
	product_owner_birth_certificate_serial varchar(256) NULL,
	product_owner_birth_certificate_number varchar(256) NULL,
	product_owner_birth_certificate_signature_private varchar(256) NULL,
	product_owner_birth_certificate_signature_public varchar(256) NULL,
	status varchar(256) NULL,
	CONSTRAINT product_pkey PRIMARY KEY (id)
);
CREATE TABLE product_aliases (
	product_id varchar(128) NOT NULL,
	alias varchar(128) NOT NULL,
	CONSTRAINT product_aliases_pkey PRIMARY KEY (product_id, alias)
);
CREATE TABLE product_owner_birth_certificate_states (
	product_id varchar(256) NOT NULL,
	state varchar(256) NOT NULL,
	CONSTRAINT product_owner_birth_certificate_states_pkey PRIMARY KEY (product_id, state)
);
CREATE TABLE product_owner_nicknames (
	product_id varchar(256) NOT NULL,
	nickname varchar(256) NOT NULL,
	CONSTRAINT product_owner_nicknames_pkey PRIMARY KEY (product_id, nickname)
);
CREATE TABLE product_owner_passport_states (
	product_id varchar(256) NOT NULL,
	state varchar(256) NOT NULL,
	CONSTRAINT product_owner_passport_states_pkey PRIMARY KEY (product_id, state)
);
CREATE TABLE product_strings (
	product_id varchar(128) NULL,
	value varchar(258) NULL
);
CREATE TABLE root_dictionary (
	id varchar(124) NOT NULL,
	CONSTRAINT root_dictionary_pkey PRIMARY KEY (id)
);
CREATE TABLE t_apicall (
	idempotenceid varchar(128) NULL,
	"data" varchar(4096) NULL,
	firstcalldate timestamp NULL
);
CREATE TABLE t_contract (
	object_id varchar(256) NOT NULL,
	code varchar(256) NULL,
	operation_id varchar(128) NULL,
	aggregateroot_id varchar(128) NULL,
	chgcnt int4 NULL,
	sys_ownerid varchar(254) NULL,
	sys_lastchangedate timestamp NULL,
	"type" varchar(128) NULL,
	CONSTRAINT t_contract_pkey PRIMARY KEY (object_id)
);
CREATE TABLE t_currency (
	object_id varchar(129) NOT NULL,
	"name" varchar(128) NULL,
	"type" varchar(128) NOT NULL,
	aggregateroot_id varchar(128) NULL,
	sys_ver     bigint,
	CONSTRAINT t_currency_pkey PRIMARY KEY (object_id)
);
CREATE TABLE t_document (
	"name" varchar(128) NULL,
	object_id varchar(254) NOT NULL,
	"type" varchar(254) NOT NULL,
	chgcnt int8 NULL,
	sys_ownerid varchar(254) NULL,
	sys_lastchangedate timestamp NULL,
	sys_ver     bigint,
	CONSTRAINT pk_t_document PRIMARY KEY (object_id)
);
CREATE INDEX i_document_type ON t_document USING btree (type);
CREATE TABLE t_documentpart (
	desc_ varchar(254) NULL,
	document_id varchar(254) NOT NULL,
	parent_id varchar(254) NULL,
	object_id varchar(254) NOT NULL,
	aggregateroot_id varchar(254) NULL,
	"type" varchar(254) NOT NULL,
	chgcnt int8 NULL,
	sys_ownerid varchar(254) NULL,
	sys_lastchangedate timestamp NULL,
	CONSTRAINT pk_t_documentpart PRIMARY KEY (object_id)
);
CREATE INDEX i_documentpart_aggregateroot_id ON t_documentpart USING btree (aggregateroot_id);
CREATE INDEX i_documentpart_document_id ON t_documentpart USING btree (document_id);
CREATE INDEX i_documentpart_parent_id ON t_documentpart USING btree (parent_id);
CREATE INDEX i_documentpart_type ON t_documentpart USING btree (type);
CREATE TABLE t_documentpartlevel2 (
	desc_ varchar(254) NULL,
	documentpart_id varchar(254) NOT NULL,
	parent_id varchar(254) NULL,
	object_id varchar(254) NOT NULL,
	aggregateroot_id varchar(254) NULL,
	"type" varchar(254) NOT NULL,
	chgcnt int8 NULL,
	sys_ownerid varchar(254) NULL,
	sys_lastchangedate timestamp NULL,
	CONSTRAINT pk_t_documentpartlevel2 PRIMARY KEY (object_id)
);
CREATE INDEX i_documentpartlevel2_aggregateroot_id ON t_documentpartlevel2 USING btree (aggregateroot_id);
CREATE INDEX i_documentpartlevel2_documentpart_id ON t_documentpartlevel2 USING btree (documentpart_id);
CREATE INDEX i_documentpartlevel2_parent_id ON t_documentpartlevel2 USING btree (parent_id);
CREATE INDEX i_documentpartlevel2_type ON t_documentpartlevel2 USING btree (type);
CREATE TABLE t_operation (
	object_id varchar(256) NOT NULL,
	code varchar(256) NULL,
	service_id varchar(128) NULL,
	aggregateroot_id varchar(128) NULL,
	chgcnt int4 NULL,
	sys_ownerid varchar(254) NULL,
	sys_lastchangedate timestamp NULL,
	"type" varchar(128) NULL,
	CONSTRAINT t_operation_pkey PRIMARY KEY (object_id)
);
create table t_product
(
  code                         varchar(254),
  attribute                    varchar(254),
  statusforplatform_code       varchar(254),
  statusforplatform_reason     varchar(254),
  sys_ver                      bigint,
  externalproduct_entityid     varchar(254),
  externalservice_entityid     varchar(254),
  externalservice_rootentityid varchar(254),
  client_entityid              varchar(254),
  object_id                    varchar(254) not null
    constraint pk_t_product
      primary key,
  type                         varchar(254) not null
);
create table t_productwithextendedstatuses
(
  description             varchar(254),
  statusforservice_code   varchar(254),
  statusforservice_reason varchar(254),
  object_id               varchar(254) not null
    constraint pk_t_productwithextendedstatuses
      primary key
);
create table t_productwithuniqueclient
(
  uniqueclient_entityid varchar(254),
  object_id             varchar(254) not null
  constraint pk_t_productwithuniqueclient
  primary key
  );
create unique index i_productwithuniqueclient_uniqueclient_entityid
  on t_productwithuniqueclient (uniqueclient_entityid);
create table t_productwithaddress
(
  name           varchar(254),
  address_city   varchar(254),
  address_street varchar(254),
  object_id      varchar(254) not null
  constraint pk_t_productwithaddress
  primary key
  );
create unique index i_productwithaddress_address_city
  on t_productwithaddress (address_city);
create unique index i_productwithaddress_name_1
  on t_productwithaddress (name, address_city, address_street);
create unique index i_productwithaddress_name
  on t_productwithaddress (name);
create table t_productwithsnowflakeid
(
  code      varchar(254),
  sys_ver   bigint,
  object_id varchar(254) not null
  constraint pk_t_productwithsnowflakeid
  primary key,
  type      varchar(254) not null
  );
create unique index i_productwithsnowflakeid_code
  on t_productwithsnowflakeid (code);
create index  i_productwithsnowflakeid_type
  on t_productwithsnowflakeid (type);
create table t_productwithsnowflakeidapicall
(
  apicallid        varchar(254),
  firstcalldate    timestamp(3),
  data             varchar(4000),
  bigdata          text,
  parentobject_id  varchar(254) not null,
  object_id        varchar(254) not null
  constraint pk_t_productwithsnowflakeidapicall
  primary key,
  aggregateroot_id varchar(254)
  );
create unique index i_productwithsnowflakeidapicall_apicallid
  on t_productwithsnowflakeidapicall (apicallid);
create index i_productwithsnowflakeidapicall_parentobject_id
  on t_productwithsnowflakeidapicall (parentobject_id);
create index i_productwithsnowflakeidapicall_aggregateroot_id
  on t_productwithsnowflakeidapicall (aggregateroot_id);
CREATE TABLE t_productapicall (
	object_id varchar(128) NOT NULL,
	apicallid varchar(128) NULL,
	"data" varchar(4096) NULL,
	bigdata varchar(4096) NULL,
	firstcalldate timestamp NULL,
	parentobject_id varchar(128) NULL,
	aggregateroot_id varchar(128) NULL,
	chgcnt int4 NULL,
	sys_lastchangedate timestamp NULL,
	sys_ownerid varchar(128) NULL,
	CONSTRAINT t_productapicall_pkey PRIMARY KEY (object_id)
);
CREATE TABLE t_rciproductexternalproducts (
	object_id varchar(256) NOT NULL,
	backreference_id varchar(128) NULL,
	reference_entityid varchar(128) NULL,
	aggregateroot_id varchar(128) NULL,
	chgcnt int4 NULL,
	sys_ownerid varchar(254) NULL,
	sys_lastchangedate timestamp NULL,
	"type" varchar(128) NULL,
	CONSTRAINT t_rciproductexternalproducts_pkey PRIMARY KEY (object_id)
);
CREATE TABLE t_rciproductexternalservices (
	object_id varchar(256) NOT NULL,
	backreference_id varchar(128) NULL,
	reference_entityid varchar(128) NULL,
	reference_rootentityid varchar(128) NULL,
	aggregateroot_id varchar(128) NULL,
	chgcnt int4 NULL,
	sys_ownerid varchar(254) NULL,
	sys_lastchangedate timestamp NULL,
	"type" varchar(128) NULL,
	CONSTRAINT t_rciproductexternalservices_pkey PRIMARY KEY (object_id)
);
CREATE TABLE t_rootdictionary (
	object_id varchar(124) NOT NULL,
	"type" varchar(128) NOT NULL,
	CONSTRAINT t_rootdictionary_pkey PRIMARY KEY (object_id)
);
CREATE TABLE t_sec_adminsettings (
	key_ varchar(254) NULL,
	rootsecurity_id varchar(254) NOT NULL,
	value_ text NULL,
	object_id varchar(254) NOT NULL,
	aggregateroot_id varchar(254) NULL,
	chgcnt int8 NULL,
	sys_isdeleted bool NOT NULL,
	sys_lastchangedate timestamp(3) NULL,
	offflag int2 NULL,
	sys_ownerid varchar(254) NULL,
	sys_partitionid int4 NULL,
	sys_recmodelversion varchar(254) NULL,
	CONSTRAINT pk_t_sec_adminsettings PRIMARY KEY (object_id)
);
CREATE INDEX i_sysadminsettings_aggregateroot_id ON t_sec_adminsettings USING btree (aggregateroot_id);
CREATE UNIQUE INDEX i_sysadminsettings_key_ ON t_sec_adminsettings USING btree (key_);
CREATE INDEX i_sysadminsettings_rootsecurity_id ON t_sec_adminsettings USING btree (rootsecurity_id);
CREATE TABLE t_sec_checkselect (
	conditionvalue text NULL,
	description varchar(254) NULL,
	operation_id varchar(254) NOT NULL,
	ordervalue int4 NULL,
	typename varchar(254) NULL,
	object_id varchar(254) NOT NULL,
	aggregateroot_id varchar(254) NULL,
	chgcnt int8 NULL,
	sys_isdeleted bool DEFAULT false NOT NULL,
	sys_lastchangedate timestamp(3) NULL,
	offflag int2 NULL,
	sys_ownerid varchar(254) NULL,
	sys_partitionid int4 DEFAULT 0 NULL,
	sys_recmodelversion varchar(254) NULL,
	beforecommitenable bool NULL,
	beforeoperationdisable bool NULL,
	CONSTRAINT pk_t_sec_checkselect PRIMARY KEY (object_id)
);
CREATE INDEX i_syscheckselect_aggregateroot_id ON t_sec_checkselect USING btree (aggregateroot_id);
CREATE INDEX i_syscheckselect_operation_id ON t_sec_checkselect USING btree (operation_id);
CREATE TABLE t_sec_operation (
	allowemptychecks bool NULL,
	body text NULL,
	disablejwtverification bool NULL,
	hashvalue varchar(254) NULL,
	rootsecurity_id varchar(254) NOT NULL,
	object_id varchar(4000) NOT NULL,
	aggregateroot_id varchar(254) NULL,
	chgcnt int8 NULL,
	sys_isdeleted bool DEFAULT false NOT NULL,
	sys_lastchangedate timestamp(3) NULL,
	offflag int2 NULL,
	sys_ownerid varchar(254) NULL,
	sys_partitionid int4 DEFAULT 0 NULL,
	sys_recmodelversion varchar(254) NULL,
	pathconditions text NULL,
	CONSTRAINT pk_t_sec_operation PRIMARY KEY (object_id)
);
CREATE INDEX i_sysoperation_aggregateroot_id ON t_sec_operation USING btree (aggregateroot_id);
CREATE INDEX i_sysoperation_rootsecurity_id ON t_sec_operation USING btree (rootsecurity_id);
CREATE TABLE t_sec_paramaddition (
	operation_id varchar(254) NOT NULL,
	paramaddition varchar(4000) NULL,
	paramname varchar(254) NULL,
	object_id varchar(254) NOT NULL,
	aggregateroot_id varchar(254) NULL,
	chgcnt int8 NULL,
	sys_isdeleted bool DEFAULT false NOT NULL,
	sys_lastchangedate timestamp(3) NULL,
	offflag int2 NULL,
	sys_ownerid varchar(254) NULL,
	sys_partitionid int4 DEFAULT 0 NULL,
	sys_recmodelversion varchar(254) NULL,
	CONSTRAINT pk_t_sec_paramaddition PRIMARY KEY (object_id)
);
CREATE INDEX i_sysparamaddition_aggregateroot_id ON t_sec_paramaddition USING btree (aggregateroot_id);
CREATE UNIQUE INDEX i_sysparamaddition_operation_id_1 ON t_sec_paramaddition USING btree (operation_id, paramname);
CREATE TABLE t_sec_rootsecurity (
	object_id varchar(254) NOT NULL,
	"type" varchar(254) NOT NULL,
	chgcnt int8 NULL,
	sys_isdeleted bool DEFAULT false NOT NULL,
	sys_lastchangedate timestamp(3) NULL,
	offflag int2 NULL,
	sys_ownerid varchar(254) NULL,
	sys_partitionid int4 DEFAULT 0 NULL,
	sys_recmodelversion varchar(254) NULL,
	sys_ver     bigint,
	CONSTRAINT pk_t_sec_rootsecurity PRIMARY KEY (object_id)
);
CREATE INDEX i_sysrootsecurity_type ON t_sec_rootsecurity USING btree (type);
CREATE TABLE t_service (
	object_id varchar(256) NOT NULL,
	code varchar(256) NULL,
	product_id varchar(128) NULL,
	aggregateroot_id varchar(128) NULL,
	chgcnt int4 NULL,
	sys_ownerid varchar(254) NULL,
	sys_lastchangedate timestamp NULL,
	"type" varchar(128) NULL,
	sys_ver     bigint,
	CONSTRAINT t_service_pkey PRIMARY KEY (object_id)
);
CREATE TABLE t_test_entity_api_call (
	id varchar(128) NOT NULL,
	"data" varchar(4096) NULL,
	firstcalldate timestamp NULL,
	parentobject_id varchar(128) NULL,
	aggregate_root varchar(128) NULL,
	CONSTRAINT t_test_entity_api_call_pkey PRIMARY KEY (id)
);
CREATE TABLE t_test_entity_ext (
	object_id varchar(128) NOT NULL,
	pstringext varchar(128) NULL,
	CONSTRAINT t_test_entity_ext_pkey PRIMARY KEY (object_id)
);
CREATE TABLE t_test_entity_single_table (
	id varchar(128) NOT NULL,
	pstringextsingletable varchar(128) NULL,
	"type" varchar(128) NULL,
	sys_ver     bigint,
	CONSTRAINT t_test_entity_single_table_pkey PRIMARY KEY (id)
);
CREATE TABLE t_testentity (
	object_id varchar(256) NOT NULL,
	pchar bpchar(1) NULL,
	pstring varchar(4000) NULL,
	ptext text NULL,
	pbyte int2 NULL,
	pshort int2 NULL,
	pinteger int4 NULL,
	plong int8 NULL,
	pfloat float4 NULL,
	pdouble float8 NULL,
	pbigdecimal numeric NULL,
	plocaldate date NULL,
	pdate timestamp(3) NULL,
	plocaltime time(6) NULL,
	plocaldatetime timestamp(6) NULL,
	poffsetdatetime timestamptz(6) NULL,
	pboolean bool NULL,
	pbytearray bytea NULL,
	chgcnt int4 NULL,
	sys_ownerid varchar(254) NULL,
	sys_lastchangedate timestamp NULL,
	owner_firstname varchar(254) NULL,
	owner_lastname varchar(254) NULL,
	"type" varchar(128) NULL,
	owner_gender varchar(128) NULL,
	sys_ver     bigint,
	CONSTRAINT t_testentity_pkey PRIMARY KEY (object_id)
);
CREATE TABLE t_testentityapicall (
	object_id varchar(128) NOT NULL,
	apicallid varchar(128) NULL,
	"data" varchar(4096) NULL,
	bigdata varchar(4096) NULL,
	firstcalldate timestamp NULL,
	parentobject_id varchar(128) NULL,
	aggregateroot_id varchar(128) NULL,
	chgcnt int4 NULL,
	sys_lastchangedate timestamp NULL,
	sys_ownerid varchar(128) NULL,
	CONSTRAINT t_testentityapicall_pkey PRIMARY KEY (object_id)
);
CREATE TABLE t_testentityext (
	object_id varchar(128) NOT NULL,
	pstringext varchar(128) NULL,
	CONSTRAINT t_testentityext_pkey PRIMARY KEY (object_id)
);
CREATE TABLE t_testentitysingletable (
	object_id varchar(256) NOT NULL,
	pstring varchar(128) NULL,
	pstringsingletableext varchar(128) NULL,
	chgcnt int4 NULL,
	sys_ownerid varchar(254) NULL,
	sys_lastchangedate timestamp NULL,
	"type" varchar(128) NULL,
	sys_ver     bigint,
	CONSTRAINT t_testentitysingletable_pkey PRIMARY KEY (object_id)
);

insert into t_rootdictionary(object_id, type)
values ('1', 'RootDictionary');
insert into t_sec_rootsecurity(object_id, type)
values ('1', 'SysRootSecurity');
