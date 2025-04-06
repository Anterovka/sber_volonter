select CURRENT_TIMESTAMP as checktime, 'Event' as entitytype, t1.object_id as id, 'T_EVENT' as table1, 'ORGANIZATION_ID' as column1, t1.ORGANIZATION_ID as value1, '' as table2, '' as column2, '' as value2, 'PARENT' as checktype, 'PNUL' as errorcode, 'the column with the parent reference is not filled' as errordetails from T_EVENT t1 where t1.ORGANIZATION_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'VolonteerEventRequest' as entitytype, t1.object_id as id, 'T_VOLONTEEREVENTREQUEST' as table1, 'VOLONTEER_ID' as column1, t1.VOLONTEER_ID as value1, '' as table2, '' as column2, '' as value2, 'PARENT' as checktype, 'PNUL' as errorcode, 'the column with the parent reference is not filled' as errordetails from T_VOLONTEEREVENTREQUEST t1 where t1.VOLONTEER_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'SysCheckSelect' as entitytype, t1.object_id as id, 'T_SEC_CHECKSELECT' as table1, 'OPERATION_ID' as column1, t1.OPERATION_ID as value1, '' as table2, '' as column2, '' as value2, 'PARENT' as checktype, 'PNUL' as errorcode, 'the column with the parent reference is not filled' as errordetails from T_SEC_CHECKSELECT t1 where t1.OPERATION_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'SysOperation' as entitytype, t1.object_id as id, 'T_SEC_OPERATION' as table1, 'ROOTSECURITY_ID' as column1, t1.ROOTSECURITY_ID as value1, '' as table2, '' as column2, '' as value2, 'PARENT' as checktype, 'PNUL' as errorcode, 'the column with the parent reference is not filled' as errordetails from T_SEC_OPERATION t1 where t1.ROOTSECURITY_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'SysAdminSettings' as entitytype, t1.object_id as id, 'T_SEC_ADMINSETTINGS' as table1, 'ROOTSECURITY_ID' as column1, t1.ROOTSECURITY_ID as value1, '' as table2, '' as column2, '' as value2, 'PARENT' as checktype, 'PNUL' as errorcode, 'the column with the parent reference is not filled' as errordetails from T_SEC_ADMINSETTINGS t1 where t1.ROOTSECURITY_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'SysParamAddition' as entitytype, t1.object_id as id, 'T_SEC_PARAMADDITION' as table1, 'OPERATION_ID' as column1, t1.OPERATION_ID as value1, '' as table2, '' as column2, '' as value2, 'PARENT' as checktype, 'PNUL' as errorcode, 'the column with the parent reference is not filled' as errordetails from T_SEC_PARAMADDITION t1 where t1.OPERATION_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'OrganizationApiCall' as entitytype, t1.object_id as id, 'T_ORGANIZATIONAPICALL' as table1, 'PARENTOBJECT_ID' as column1, t1.PARENTOBJECT_ID as value1, '' as table2, '' as column2, '' as value2, 'PARENT' as checktype, 'PNUL' as errorcode, 'the column with the parent reference is not filled' as errordetails from T_ORGANIZATIONAPICALL t1 where t1.PARENTOBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'PersonApiCall' as entitytype, t1.object_id as id, 'T_PERSONAPICALL' as table1, 'PARENTOBJECT_ID' as column1, t1.PARENTOBJECT_ID as value1, '' as table2, '' as column2, '' as value2, 'PARENT' as checktype, 'PNUL' as errorcode, 'the column with the parent reference is not filled' as errordetails from T_PERSONAPICALL t1 where t1.PARENTOBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'VolonteerApiCall' as entitytype, t1.object_id as id, 'T_VOLONTEERAPICALL' as table1, 'PARENTOBJECT_ID' as column1, t1.PARENTOBJECT_ID as value1, '' as table2, '' as column2, '' as value2, 'PARENT' as checktype, 'PNUL' as errorcode, 'the column with the parent reference is not filled' as errordetails from T_VOLONTEERAPICALL t1 where t1.PARENTOBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'Event' as entitytype, t1.object_id as id, 'T_EVENT' as table1, 'ORGANIZATION_ID' as column1, t1.ORGANIZATION_ID as value1, 'T_ORGANIZATION' as table2, 'object_id' as column2, '' as value2, 'PARENT' as checktype, 'PMIS' as errorcode, 'a row is missing in the parent table of aggregate T_ORGANIZATION' as errordetails from T_EVENT t1 left join T_ORGANIZATION t2 on t1.ORGANIZATION_ID = t2.object_id where t2.object_id is null union all
select CURRENT_TIMESTAMP as checktime, 'VolonteerEventRequest' as entitytype, t1.object_id as id, 'T_VOLONTEEREVENTREQUEST' as table1, 'VOLONTEER_ID' as column1, t1.VOLONTEER_ID as value1, 'T_VOLONTEER' as table2, 'object_id' as column2, '' as value2, 'PARENT' as checktype, 'PMIS' as errorcode, 'a row is missing in the parent table of aggregate T_VOLONTEER' as errordetails from T_VOLONTEEREVENTREQUEST t1 left join T_VOLONTEER t2 on t1.VOLONTEER_ID = t2.object_id where t2.object_id is null union all
select CURRENT_TIMESTAMP as checktime, 'SysCheckSelect' as entitytype, t1.object_id as id, 'T_SEC_CHECKSELECT' as table1, 'OPERATION_ID' as column1, t1.OPERATION_ID as value1, 'T_SEC_OPERATION' as table2, 'object_id' as column2, '' as value2, 'PARENT' as checktype, 'PMIS' as errorcode, 'a row is missing in the parent table of aggregate T_SEC_OPERATION' as errordetails from T_SEC_CHECKSELECT t1 left join T_SEC_OPERATION t2 on t1.OPERATION_ID = t2.object_id where t2.object_id is null union all
select CURRENT_TIMESTAMP as checktime, 'SysOperation' as entitytype, t1.object_id as id, 'T_SEC_OPERATION' as table1, 'ROOTSECURITY_ID' as column1, t1.ROOTSECURITY_ID as value1, 'T_SEC_ROOTSECURITY' as table2, 'object_id' as column2, '' as value2, 'PARENT' as checktype, 'PMIS' as errorcode, 'a row is missing in the parent table of aggregate T_SEC_ROOTSECURITY' as errordetails from T_SEC_OPERATION t1 left join T_SEC_ROOTSECURITY t2 on t1.ROOTSECURITY_ID = t2.object_id where t2.object_id is null union all
select CURRENT_TIMESTAMP as checktime, 'SysAdminSettings' as entitytype, t1.object_id as id, 'T_SEC_ADMINSETTINGS' as table1, 'ROOTSECURITY_ID' as column1, t1.ROOTSECURITY_ID as value1, 'T_SEC_ROOTSECURITY' as table2, 'object_id' as column2, '' as value2, 'PARENT' as checktype, 'PMIS' as errorcode, 'a row is missing in the parent table of aggregate T_SEC_ROOTSECURITY' as errordetails from T_SEC_ADMINSETTINGS t1 left join T_SEC_ROOTSECURITY t2 on t1.ROOTSECURITY_ID = t2.object_id where t2.object_id is null union all
select CURRENT_TIMESTAMP as checktime, 'SysParamAddition' as entitytype, t1.object_id as id, 'T_SEC_PARAMADDITION' as table1, 'OPERATION_ID' as column1, t1.OPERATION_ID as value1, 'T_SEC_OPERATION' as table2, 'object_id' as column2, '' as value2, 'PARENT' as checktype, 'PMIS' as errorcode, 'a row is missing in the parent table of aggregate T_SEC_OPERATION' as errordetails from T_SEC_PARAMADDITION t1 left join T_SEC_OPERATION t2 on t1.OPERATION_ID = t2.object_id where t2.object_id is null union all
select CURRENT_TIMESTAMP as checktime, 'OrganizationApiCall' as entitytype, t1.object_id as id, 'T_ORGANIZATIONAPICALL' as table1, 'PARENTOBJECT_ID' as column1, t1.PARENTOBJECT_ID as value1, 'T_ORGANIZATION' as table2, 'object_id' as column2, '' as value2, 'PARENT' as checktype, 'PMIS' as errorcode, 'a row is missing in the parent table of aggregate T_ORGANIZATION' as errordetails from T_ORGANIZATIONAPICALL t1 left join T_ORGANIZATION t2 on t1.PARENTOBJECT_ID = t2.object_id where t2.object_id is null union all
select CURRENT_TIMESTAMP as checktime, 'PersonApiCall' as entitytype, t1.object_id as id, 'T_PERSONAPICALL' as table1, 'PARENTOBJECT_ID' as column1, t1.PARENTOBJECT_ID as value1, 'T_PERSON' as table2, 'object_id' as column2, '' as value2, 'PARENT' as checktype, 'PMIS' as errorcode, 'a row is missing in the parent table of aggregate T_PERSON' as errordetails from T_PERSONAPICALL t1 left join T_PERSON t2 on t1.PARENTOBJECT_ID = t2.object_id where t2.object_id is null union all
select CURRENT_TIMESTAMP as checktime, 'VolonteerApiCall' as entitytype, t1.object_id as id, 'T_VOLONTEERAPICALL' as table1, 'PARENTOBJECT_ID' as column1, t1.PARENTOBJECT_ID as value1, 'T_VOLONTEER' as table2, 'object_id' as column2, '' as value2, 'PARENT' as checktype, 'PMIS' as errorcode, 'a row is missing in the parent table of aggregate T_VOLONTEER' as errordetails from T_VOLONTEERAPICALL t1 left join T_VOLONTEER t2 on t1.PARENTOBJECT_ID = t2.object_id where t2.object_id is null union all
select CURRENT_TIMESTAMP as checktime, 'Event' as entitytype, t1.object_id as id, 'T_EVENT' as table1, 'AGGREGATEROOT_ID' as column1, t1.AGGREGATEROOT_ID as value1, '' as table2, '' as column2, '' as value2, 'AGGREGATE_ROOT' as checktype, 'ANUL' as errorcode, 'the column containing the reference to the aggregate root is not filled' as errordetails from T_EVENT t1 where t1.aggregateroot_id is null union all
select CURRENT_TIMESTAMP as checktime, 'VolonteerEventRequest' as entitytype, t1.object_id as id, 'T_VOLONTEEREVENTREQUEST' as table1, 'AGGREGATEROOT_ID' as column1, t1.AGGREGATEROOT_ID as value1, '' as table2, '' as column2, '' as value2, 'AGGREGATE_ROOT' as checktype, 'ANUL' as errorcode, 'the column containing the reference to the aggregate root is not filled' as errordetails from T_VOLONTEEREVENTREQUEST t1 where t1.aggregateroot_id is null union all
select CURRENT_TIMESTAMP as checktime, 'SysCheckSelect' as entitytype, t1.object_id as id, 'T_SEC_CHECKSELECT' as table1, 'AGGREGATEROOT_ID' as column1, t1.AGGREGATEROOT_ID as value1, '' as table2, '' as column2, '' as value2, 'AGGREGATE_ROOT' as checktype, 'ANUL' as errorcode, 'the column containing the reference to the aggregate root is not filled' as errordetails from T_SEC_CHECKSELECT t1 where t1.aggregateroot_id is null union all
select CURRENT_TIMESTAMP as checktime, 'SysOperation' as entitytype, t1.object_id as id, 'T_SEC_OPERATION' as table1, 'AGGREGATEROOT_ID' as column1, t1.AGGREGATEROOT_ID as value1, '' as table2, '' as column2, '' as value2, 'AGGREGATE_ROOT' as checktype, 'ANUL' as errorcode, 'the column containing the reference to the aggregate root is not filled' as errordetails from T_SEC_OPERATION t1 where t1.aggregateroot_id is null union all
select CURRENT_TIMESTAMP as checktime, 'SysAdminSettings' as entitytype, t1.object_id as id, 'T_SEC_ADMINSETTINGS' as table1, 'AGGREGATEROOT_ID' as column1, t1.AGGREGATEROOT_ID as value1, '' as table2, '' as column2, '' as value2, 'AGGREGATE_ROOT' as checktype, 'ANUL' as errorcode, 'the column containing the reference to the aggregate root is not filled' as errordetails from T_SEC_ADMINSETTINGS t1 where t1.aggregateroot_id is null union all
select CURRENT_TIMESTAMP as checktime, 'SysParamAddition' as entitytype, t1.object_id as id, 'T_SEC_PARAMADDITION' as table1, 'AGGREGATEROOT_ID' as column1, t1.AGGREGATEROOT_ID as value1, '' as table2, '' as column2, '' as value2, 'AGGREGATE_ROOT' as checktype, 'ANUL' as errorcode, 'the column containing the reference to the aggregate root is not filled' as errordetails from T_SEC_PARAMADDITION t1 where t1.aggregateroot_id is null union all
select CURRENT_TIMESTAMP as checktime, 'OrganizationApiCall' as entitytype, t1.object_id as id, 'T_ORGANIZATIONAPICALL' as table1, 'AGGREGATEROOT_ID' as column1, t1.AGGREGATEROOT_ID as value1, '' as table2, '' as column2, '' as value2, 'AGGREGATE_ROOT' as checktype, 'ANUL' as errorcode, 'the column containing the reference to the aggregate root is not filled' as errordetails from T_ORGANIZATIONAPICALL t1 where t1.aggregateroot_id is null union all
select CURRENT_TIMESTAMP as checktime, 'PersonApiCall' as entitytype, t1.object_id as id, 'T_PERSONAPICALL' as table1, 'AGGREGATEROOT_ID' as column1, t1.AGGREGATEROOT_ID as value1, '' as table2, '' as column2, '' as value2, 'AGGREGATE_ROOT' as checktype, 'ANUL' as errorcode, 'the column containing the reference to the aggregate root is not filled' as errordetails from T_PERSONAPICALL t1 where t1.aggregateroot_id is null union all
select CURRENT_TIMESTAMP as checktime, 'VolonteerApiCall' as entitytype, t1.object_id as id, 'T_VOLONTEERAPICALL' as table1, 'AGGREGATEROOT_ID' as column1, t1.AGGREGATEROOT_ID as value1, '' as table2, '' as column2, '' as value2, 'AGGREGATE_ROOT' as checktype, 'ANUL' as errorcode, 'the column containing the reference to the aggregate root is not filled' as errordetails from T_VOLONTEERAPICALL t1 where t1.aggregateroot_id is null union all
select CURRENT_TIMESTAMP as checktime, 'Event' as entitytype, t1.object_id as id, 'T_EVENT' as table1, 'AGGREGATEROOT_ID' as column1, t1.AGGREGATEROOT_ID as value1, 'T_ORGANIZATION' as table2, 'object_id' as column2, '' as value2, 'AGGREGATE_ROOT' as checktype, 'AMIS' as errorcode, 'the string is missing in the aggregate root table T_ORGANIZATION' as errordetails from T_EVENT t1 left join T_ORGANIZATION t2 on t1.aggregateroot_id = t2.object_id where t2.object_id is null union all
select CURRENT_TIMESTAMP as checktime, 'VolonteerEventRequest' as entitytype, t1.object_id as id, 'T_VOLONTEEREVENTREQUEST' as table1, 'AGGREGATEROOT_ID' as column1, t1.AGGREGATEROOT_ID as value1, 'T_VOLONTEER' as table2, 'object_id' as column2, '' as value2, 'AGGREGATE_ROOT' as checktype, 'AMIS' as errorcode, 'the string is missing in the aggregate root table T_VOLONTEER' as errordetails from T_VOLONTEEREVENTREQUEST t1 left join T_VOLONTEER t2 on t1.aggregateroot_id = t2.object_id where t2.object_id is null union all
select CURRENT_TIMESTAMP as checktime, 'SysCheckSelect' as entitytype, t1.object_id as id, 'T_SEC_CHECKSELECT' as table1, 'AGGREGATEROOT_ID' as column1, t1.AGGREGATEROOT_ID as value1, 'T_SEC_ROOTSECURITY' as table2, 'object_id' as column2, '' as value2, 'AGGREGATE_ROOT' as checktype, 'AMIS' as errorcode, 'the string is missing in the aggregate root table T_SEC_ROOTSECURITY' as errordetails from T_SEC_CHECKSELECT t1 left join T_SEC_ROOTSECURITY t2 on t1.aggregateroot_id = t2.object_id where t2.object_id is null union all
select CURRENT_TIMESTAMP as checktime, 'SysOperation' as entitytype, t1.object_id as id, 'T_SEC_OPERATION' as table1, 'AGGREGATEROOT_ID' as column1, t1.AGGREGATEROOT_ID as value1, 'T_SEC_ROOTSECURITY' as table2, 'object_id' as column2, '' as value2, 'AGGREGATE_ROOT' as checktype, 'AMIS' as errorcode, 'the string is missing in the aggregate root table T_SEC_ROOTSECURITY' as errordetails from T_SEC_OPERATION t1 left join T_SEC_ROOTSECURITY t2 on t1.aggregateroot_id = t2.object_id where t2.object_id is null union all
select CURRENT_TIMESTAMP as checktime, 'SysAdminSettings' as entitytype, t1.object_id as id, 'T_SEC_ADMINSETTINGS' as table1, 'AGGREGATEROOT_ID' as column1, t1.AGGREGATEROOT_ID as value1, 'T_SEC_ROOTSECURITY' as table2, 'object_id' as column2, '' as value2, 'AGGREGATE_ROOT' as checktype, 'AMIS' as errorcode, 'the string is missing in the aggregate root table T_SEC_ROOTSECURITY' as errordetails from T_SEC_ADMINSETTINGS t1 left join T_SEC_ROOTSECURITY t2 on t1.aggregateroot_id = t2.object_id where t2.object_id is null union all
select CURRENT_TIMESTAMP as checktime, 'SysParamAddition' as entitytype, t1.object_id as id, 'T_SEC_PARAMADDITION' as table1, 'AGGREGATEROOT_ID' as column1, t1.AGGREGATEROOT_ID as value1, 'T_SEC_ROOTSECURITY' as table2, 'object_id' as column2, '' as value2, 'AGGREGATE_ROOT' as checktype, 'AMIS' as errorcode, 'the string is missing in the aggregate root table T_SEC_ROOTSECURITY' as errordetails from T_SEC_PARAMADDITION t1 left join T_SEC_ROOTSECURITY t2 on t1.aggregateroot_id = t2.object_id where t2.object_id is null union all
select CURRENT_TIMESTAMP as checktime, 'OrganizationApiCall' as entitytype, t1.object_id as id, 'T_ORGANIZATIONAPICALL' as table1, 'AGGREGATEROOT_ID' as column1, t1.AGGREGATEROOT_ID as value1, 'T_ORGANIZATION' as table2, 'object_id' as column2, '' as value2, 'AGGREGATE_ROOT' as checktype, 'AMIS' as errorcode, 'the string is missing in the aggregate root table T_ORGANIZATION' as errordetails from T_ORGANIZATIONAPICALL t1 left join T_ORGANIZATION t2 on t1.aggregateroot_id = t2.object_id where t2.object_id is null union all
select CURRENT_TIMESTAMP as checktime, 'PersonApiCall' as entitytype, t1.object_id as id, 'T_PERSONAPICALL' as table1, 'AGGREGATEROOT_ID' as column1, t1.AGGREGATEROOT_ID as value1, 'T_PERSON' as table2, 'object_id' as column2, '' as value2, 'AGGREGATE_ROOT' as checktype, 'AMIS' as errorcode, 'the string is missing in the aggregate root table T_PERSON' as errordetails from T_PERSONAPICALL t1 left join T_PERSON t2 on t1.aggregateroot_id = t2.object_id where t2.object_id is null union all
select CURRENT_TIMESTAMP as checktime, 'VolonteerApiCall' as entitytype, t1.object_id as id, 'T_VOLONTEERAPICALL' as table1, 'AGGREGATEROOT_ID' as column1, t1.AGGREGATEROOT_ID as value1, 'T_VOLONTEER' as table2, 'object_id' as column2, '' as value2, 'AGGREGATE_ROOT' as checktype, 'AMIS' as errorcode, 'the string is missing in the aggregate root table T_VOLONTEER' as errordetails from T_VOLONTEERAPICALL t1 left join T_VOLONTEER t2 on t1.aggregateroot_id = t2.object_id where t2.object_id is null union all
select CURRENT_TIMESTAMP as checktime, 'Event' as entitytype, t1.object_id as id, 'T_EVENT' as table1, 'DESCRIPTION' as column1, cast(t1.DESCRIPTION as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_EVENT t1 where t1.DESCRIPTION is null union all
select CURRENT_TIMESTAMP as checktime, 'Event' as entitytype, t1.object_id as id, 'T_EVENT' as table1, 'ORGANIZATION_ID' as column1, cast(t1.ORGANIZATION_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_EVENT t1 where t1.ORGANIZATION_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'Event' as entitytype, t1.object_id as id, 'T_EVENT' as table1, 'OBJECT_ID' as column1, cast(t1.OBJECT_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_EVENT t1 where t1.OBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'Event' as entitytype, t1.object_id as id, 'T_EVENT' as table1, 'TYPE' as column1, cast(t1.TYPE as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_EVENT t1 where t1.TYPE is null union all
select CURRENT_TIMESTAMP as checktime, 'Organization' as entitytype, t1.object_id as id, 'T_ORGANIZATION' as table1, 'NAME' as column1, cast(t1.NAME as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_ORGANIZATION t1 where t1.NAME is null union all
select CURRENT_TIMESTAMP as checktime, 'Organization' as entitytype, t1.object_id as id, 'T_ORGANIZATION' as table1, 'OBJECT_ID' as column1, cast(t1.OBJECT_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_ORGANIZATION t1 where t1.OBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'Organization' as entitytype, t1.object_id as id, 'T_ORGANIZATION' as table1, 'TYPE' as column1, cast(t1.TYPE as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_ORGANIZATION t1 where t1.TYPE is null union all
select CURRENT_TIMESTAMP as checktime, 'Person' as entitytype, t1.object_id as id, 'T_PERSON' as table1, 'FIRSTNAME' as column1, cast(t1.FIRSTNAME as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_PERSON t1 where t1.FIRSTNAME is null union all
select CURRENT_TIMESTAMP as checktime, 'Person' as entitytype, t1.object_id as id, 'T_PERSON' as table1, 'LASTNAME' as column1, cast(t1.LASTNAME as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_PERSON t1 where t1.LASTNAME is null union all
select CURRENT_TIMESTAMP as checktime, 'Person' as entitytype, t1.object_id as id, 'T_PERSON' as table1, 'OBJECT_ID' as column1, cast(t1.OBJECT_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_PERSON t1 where t1.OBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'Person' as entitytype, t1.object_id as id, 'T_PERSON' as table1, 'TYPE' as column1, cast(t1.TYPE as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_PERSON t1 where t1.TYPE is null union all
select CURRENT_TIMESTAMP as checktime, 'Volonteer' as entitytype, t1.object_id as id, 'T_VOLONTEER' as table1, 'OBJECT_ID' as column1, cast(t1.OBJECT_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_VOLONTEER t1 where t1.OBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'Volonteer' as entitytype, t1.object_id as id, 'T_VOLONTEER' as table1, 'TYPE' as column1, cast(t1.TYPE as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_VOLONTEER t1 where t1.TYPE is null union all
select CURRENT_TIMESTAMP as checktime, 'VolonteerEventRequest' as entitytype, t1.object_id as id, 'T_VOLONTEEREVENTREQUEST' as table1, 'VOLONTEER_ID' as column1, cast(t1.VOLONTEER_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_VOLONTEEREVENTREQUEST t1 where t1.VOLONTEER_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'VolonteerEventRequest' as entitytype, t1.object_id as id, 'T_VOLONTEEREVENTREQUEST' as table1, 'OBJECT_ID' as column1, cast(t1.OBJECT_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_VOLONTEEREVENTREQUEST t1 where t1.OBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'VolonteerEventRequest' as entitytype, t1.object_id as id, 'T_VOLONTEEREVENTREQUEST' as table1, 'TYPE' as column1, cast(t1.TYPE as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_VOLONTEEREVENTREQUEST t1 where t1.TYPE is null union all
select CURRENT_TIMESTAMP as checktime, 'RootDictionary' as entitytype, t1.object_id as id, 'T_ROOTDICTIONARY' as table1, 'OBJECT_ID' as column1, cast(t1.OBJECT_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_ROOTDICTIONARY t1 where t1.OBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'RootDictionary' as entitytype, t1.object_id as id, 'T_ROOTDICTIONARY' as table1, 'TYPE' as column1, cast(t1.TYPE as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_ROOTDICTIONARY t1 where t1.TYPE is null union all
select CURRENT_TIMESTAMP as checktime, 'SysRootSecurity' as entitytype, t1.object_id as id, 'T_SEC_ROOTSECURITY' as table1, 'OBJECT_ID' as column1, cast(t1.OBJECT_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_SEC_ROOTSECURITY t1 where t1.OBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'SysRootSecurity' as entitytype, t1.object_id as id, 'T_SEC_ROOTSECURITY' as table1, 'TYPE' as column1, cast(t1.TYPE as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_SEC_ROOTSECURITY t1 where t1.TYPE is null union all
select CURRENT_TIMESTAMP as checktime, 'SysCheckSelect' as entitytype, t1.object_id as id, 'T_SEC_CHECKSELECT' as table1, 'OPERATION_ID' as column1, cast(t1.OPERATION_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_SEC_CHECKSELECT t1 where t1.OPERATION_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'SysCheckSelect' as entitytype, t1.object_id as id, 'T_SEC_CHECKSELECT' as table1, 'OBJECT_ID' as column1, cast(t1.OBJECT_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_SEC_CHECKSELECT t1 where t1.OBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'SysOperation' as entitytype, t1.object_id as id, 'T_SEC_OPERATION' as table1, 'ROOTSECURITY_ID' as column1, cast(t1.ROOTSECURITY_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_SEC_OPERATION t1 where t1.ROOTSECURITY_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'SysOperation' as entitytype, t1.object_id as id, 'T_SEC_OPERATION' as table1, 'OBJECT_ID' as column1, cast(t1.OBJECT_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_SEC_OPERATION t1 where t1.OBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'SysAdminSettings' as entitytype, t1.object_id as id, 'T_SEC_ADMINSETTINGS' as table1, 'ROOTSECURITY_ID' as column1, cast(t1.ROOTSECURITY_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_SEC_ADMINSETTINGS t1 where t1.ROOTSECURITY_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'SysAdminSettings' as entitytype, t1.object_id as id, 'T_SEC_ADMINSETTINGS' as table1, 'OBJECT_ID' as column1, cast(t1.OBJECT_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_SEC_ADMINSETTINGS t1 where t1.OBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'SysParamAddition' as entitytype, t1.object_id as id, 'T_SEC_PARAMADDITION' as table1, 'OPERATION_ID' as column1, cast(t1.OPERATION_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_SEC_PARAMADDITION t1 where t1.OPERATION_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'SysParamAddition' as entitytype, t1.object_id as id, 'T_SEC_PARAMADDITION' as table1, 'OBJECT_ID' as column1, cast(t1.OBJECT_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_SEC_PARAMADDITION t1 where t1.OBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'OrganizationApiCall' as entitytype, t1.object_id as id, 'T_ORGANIZATIONAPICALL' as table1, 'PARENTOBJECT_ID' as column1, cast(t1.PARENTOBJECT_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_ORGANIZATIONAPICALL t1 where t1.PARENTOBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'OrganizationApiCall' as entitytype, t1.object_id as id, 'T_ORGANIZATIONAPICALL' as table1, 'OBJECT_ID' as column1, cast(t1.OBJECT_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_ORGANIZATIONAPICALL t1 where t1.OBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'PersonApiCall' as entitytype, t1.object_id as id, 'T_PERSONAPICALL' as table1, 'PARENTOBJECT_ID' as column1, cast(t1.PARENTOBJECT_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_PERSONAPICALL t1 where t1.PARENTOBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'PersonApiCall' as entitytype, t1.object_id as id, 'T_PERSONAPICALL' as table1, 'OBJECT_ID' as column1, cast(t1.OBJECT_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_PERSONAPICALL t1 where t1.OBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'VolonteerApiCall' as entitytype, t1.object_id as id, 'T_VOLONTEERAPICALL' as table1, 'PARENTOBJECT_ID' as column1, cast(t1.PARENTOBJECT_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_VOLONTEERAPICALL t1 where t1.PARENTOBJECT_ID is null union all
select CURRENT_TIMESTAMP as checktime, 'VolonteerApiCall' as entitytype, t1.object_id as id, 'T_VOLONTEERAPICALL' as table1, 'OBJECT_ID' as column1, cast(t1.OBJECT_ID as varchar(254)) as value1, '' as table2, '' as column2, '' as value2, 'MANDATORY' as checktype, 'MNUL' as errorcode, 'обязательное значение не заполнено' as errordetails from T_VOLONTEERAPICALL t1 where t1.OBJECT_ID is null