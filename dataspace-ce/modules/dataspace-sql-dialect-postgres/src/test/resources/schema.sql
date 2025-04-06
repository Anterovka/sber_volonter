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
