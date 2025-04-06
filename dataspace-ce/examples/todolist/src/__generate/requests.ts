import { gql } from '@apollo/client';
import * as Apollo from '@apollo/client';
export type Maybe<T> = T | null;
export type InputMaybe<T> = Maybe<T>;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
export type MakeOptional<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]: Maybe<T[SubKey]> };
export type MakeEmpty<T extends { [key: string]: unknown }, K extends keyof T> = { [_ in K]?: never };
export type Incremental<T> = T | { [P in keyof T]?: P extends ' $fragmentName' | '__typename' ? T[P] : never };
const defaultOptions = {} as const;
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: { input: string; output: string; }
  String: { input: string; output: string; }
  Boolean: { input: boolean; output: boolean; }
  Int: { input: number; output: number; }
  Float: { input: number; output: number; }
  /** An arbitrary precision signed decimal */
  BigDecimal: { input: any; output: any; }
  /** An 8-bit signed integer */
  Byte: { input: any; output: any; }
  /** A UTF-16 code unit; a character on Unicode's BMP */
  Char: { input: any; output: any; }
  /** A 64-bit signed integer */
  Long: { input: any; output: any; }
  /** A 16-bit signed integer */
  Short: { input: any; output: any; }
  _ByteArray: { input: any; output: any; }
  _Date: { input: any; output: any; }
  _DateTime: { input: any; output: any; }
  _Float4: { input: any; output: any; }
  _OffsetDateTime: { input: any; output: any; }
  _Time: { input: any; output: any; }
};

export type Customer = {
  _calc: _Calculation;
  aggVersion: Scalars['Long']['output'];
  chgCnt?: Maybe<Scalars['Long']['output']>;
  email?: Maybe<Scalars['String']['output']>;
  id: Scalars['ID']['output'];
  lastChangeDate?: Maybe<Scalars['_DateTime']['output']>;
  login?: Maybe<Scalars['String']['output']>;
  ownerId?: Maybe<Scalars['String']['output']>;
  type: Scalars['String']['output'];
};

export type RootDictionary = {
  _calc: _Calculation;
  id: Scalars['ID']['output'];
  type: Scalars['String']['output'];
};

export type Stakeholder = {
  _calc: _Calculation;
  aggregateRoot?: Maybe<RootDictionary>;
  chgCnt?: Maybe<Scalars['Long']['output']>;
  code?: Maybe<Scalars['String']['output']>;
  id: Scalars['ID']['output'];
  lastChangeDate?: Maybe<Scalars['_DateTime']['output']>;
  name?: Maybe<Scalars['String']['output']>;
};


export type StakeholderAggregateRootArgs = {
  alias?: InputMaybe<Scalars['String']['input']>;
};

export type Status = {
  _calc: _Calculation;
  aggregateRoot?: Maybe<RootDictionary>;
  chgCnt?: Maybe<Scalars['Long']['output']>;
  code?: Maybe<Scalars['String']['output']>;
  description?: Maybe<Scalars['String']['output']>;
  id: Scalars['ID']['output'];
  initial?: Maybe<Scalars['Boolean']['output']>;
  lastChangeDate?: Maybe<Scalars['_DateTime']['output']>;
  name?: Maybe<Scalars['String']['output']>;
  stakeholder?: Maybe<Stakeholder>;
  statusType?: Maybe<Scalars['String']['output']>;
};


export type StatusAggregateRootArgs = {
  alias?: InputMaybe<Scalars['String']['input']>;
};


export type StatusStakeholderArgs = {
  alias?: InputMaybe<Scalars['String']['input']>;
};

export type StatusGraph = {
  _calc: _Calculation;
  aggregateRoot?: Maybe<RootDictionary>;
  chgCnt?: Maybe<Scalars['Long']['output']>;
  code?: Maybe<Scalars['String']['output']>;
  id: Scalars['ID']['output'];
  label?: Maybe<Scalars['String']['output']>;
  lastChangeDate?: Maybe<Scalars['_DateTime']['output']>;
  name?: Maybe<Scalars['String']['output']>;
  statusFrom?: Maybe<Status>;
  statusTo?: Maybe<Status>;
};


export type StatusGraphAggregateRootArgs = {
  alias?: InputMaybe<Scalars['String']['input']>;
};


export type StatusGraphStatusFromArgs = {
  alias?: InputMaybe<Scalars['String']['input']>;
};


export type StatusGraphStatusToArgs = {
  alias?: InputMaybe<Scalars['String']['input']>;
};

export type Task = {
  _calc: _Calculation;
  accessList: _Ec_TaskCustomerAccess;
  aggVersion: Scalars['Long']['output'];
  chgCnt?: Maybe<Scalars['Long']['output']>;
  customer: _G_CustomerReference;
  description?: Maybe<Scalars['String']['output']>;
  dueDate?: Maybe<Scalars['_DateTime']['output']>;
  id: Scalars['ID']['output'];
  lastChangeDate?: Maybe<Scalars['_DateTime']['output']>;
  ownerId?: Maybe<Scalars['String']['output']>;
  status?: Maybe<_En_TaskStatus>;
  tags: _Enc_TaskTag;
  timeStamp?: Maybe<Scalars['_DateTime']['output']>;
  title?: Maybe<Scalars['String']['output']>;
  type: Scalars['String']['output'];
};


export type TaskAccessListArgs = {
  cond?: InputMaybe<Scalars['String']['input']>;
  elemAlias?: InputMaybe<Scalars['String']['input']>;
  limit?: InputMaybe<Scalars['Int']['input']>;
  offset?: InputMaybe<Scalars['Int']['input']>;
  sort?: InputMaybe<Array<_SortCriterionSpecification>>;
};


export type TaskTagsArgs = {
  cond?: InputMaybe<Scalars['String']['input']>;
  limit?: InputMaybe<Scalars['Int']['input']>;
  offset?: InputMaybe<Scalars['Int']['input']>;
  sort?: InputMaybe<Array<_SortCriterionSpecification>>;
};

export type TaskCustomerAccess = {
  _calc: _Calculation;
  accessType?: Maybe<_En_AccessType>;
  aggVersion: Scalars['Long']['output'];
  aggregateRoot?: Maybe<Task>;
  chgCnt?: Maybe<Scalars['Long']['output']>;
  customer: _G_CustomerReference;
  id: Scalars['ID']['output'];
  lastChangeDate?: Maybe<Scalars['_DateTime']['output']>;
  ownerId?: Maybe<Scalars['String']['output']>;
  task: Task;
  type: Scalars['String']['output'];
};


export type TaskCustomerAccessAggregateRootArgs = {
  alias?: InputMaybe<Scalars['String']['input']>;
};


export type TaskCustomerAccessTaskArgs = {
  alias?: InputMaybe<Scalars['String']['input']>;
};

export type _Calculation = {
  __typename?: '_Calculation';
  bigDecimal?: Maybe<Scalars['BigDecimal']['output']>;
  boolean?: Maybe<Scalars['Boolean']['output']>;
  byte?: Maybe<Scalars['Byte']['output']>;
  byteArray?: Maybe<Scalars['_ByteArray']['output']>;
  char?: Maybe<Scalars['Char']['output']>;
  date?: Maybe<Scalars['_Date']['output']>;
  dateTime?: Maybe<Scalars['_DateTime']['output']>;
  double?: Maybe<Scalars['Float']['output']>;
  float?: Maybe<Scalars['_Float4']['output']>;
  int?: Maybe<Scalars['Int']['output']>;
  long?: Maybe<Scalars['Long']['output']>;
  offsetDateTime?: Maybe<Scalars['_OffsetDateTime']['output']>;
  short?: Maybe<Scalars['Short']['output']>;
  string?: Maybe<Scalars['String']['output']>;
  time?: Maybe<Scalars['_Time']['output']>;
};


export type _CalculationBigDecimalArgs = {
  expr: Scalars['String']['input'];
};


export type _CalculationBooleanArgs = {
  expr: Scalars['String']['input'];
};


export type _CalculationByteArgs = {
  expr: Scalars['String']['input'];
};


export type _CalculationByteArrayArgs = {
  expr: Scalars['String']['input'];
};


export type _CalculationCharArgs = {
  expr: Scalars['String']['input'];
};


export type _CalculationDateArgs = {
  expr: Scalars['String']['input'];
};


export type _CalculationDateTimeArgs = {
  expr: Scalars['String']['input'];
};


export type _CalculationDoubleArgs = {
  expr: Scalars['String']['input'];
};


export type _CalculationFloatArgs = {
  expr: Scalars['String']['input'];
};


export type _CalculationIntArgs = {
  expr: Scalars['String']['input'];
};


export type _CalculationLongArgs = {
  expr: Scalars['String']['input'];
};


export type _CalculationOffsetDateTimeArgs = {
  expr: Scalars['String']['input'];
};


export type _CalculationShortArgs = {
  expr: Scalars['String']['input'];
};


export type _CalculationStringArgs = {
  expr: Scalars['String']['input'];
};


export type _CalculationTimeArgs = {
  expr: Scalars['String']['input'];
};

export type _CompareCustomerInput = {
  _expr?: InputMaybe<Array<InputMaybe<_GenericExprInput>>>;
  email?: InputMaybe<Scalars['String']['input']>;
  login?: InputMaybe<Scalars['String']['input']>;
};

export type _CompareTaskCustomerAccessInput = {
  _expr?: InputMaybe<Array<InputMaybe<_GenericExprInput>>>;
  accessType?: InputMaybe<_En_AccessType>;
};

export type _CompareTaskInput = {
  _expr?: InputMaybe<Array<InputMaybe<_GenericExprInput>>>;
  dueDate?: InputMaybe<Scalars['_DateTime']['input']>;
  status?: InputMaybe<_En_TaskStatus>;
  timeStamp?: InputMaybe<Scalars['_DateTime']['input']>;
  title?: InputMaybe<Scalars['String']['input']>;
};

export type _CreateCustomerInput = {
  _expr?: InputMaybe<Array<InputMaybe<_GenericExprInput>>>;
  email?: InputMaybe<Scalars['String']['input']>;
  id: Scalars['ID']['input'];
  login?: InputMaybe<Scalars['String']['input']>;
};

export type _CreateTaskCustomerAccessInput = {
  _expr?: InputMaybe<Array<InputMaybe<_GenericExprInput>>>;
  accessType?: InputMaybe<_En_AccessType>;
  customer?: InputMaybe<_SingleReferenceInput>;
  task: Scalars['ID']['input'];
};

export type _CreateTaskInput = {
  _expr?: InputMaybe<Array<InputMaybe<_GenericExprInput>>>;
  customer?: InputMaybe<_SingleReferenceInput>;
  description?: InputMaybe<Scalars['String']['input']>;
  dueDate?: InputMaybe<Scalars['_DateTime']['input']>;
  status?: InputMaybe<_En_TaskStatus>;
  tags?: InputMaybe<Array<InputMaybe<_En_TaskTag>>>;
  timeStamp?: InputMaybe<Scalars['_DateTime']['input']>;
  title?: InputMaybe<Scalars['String']['input']>;
};

export type _DeleteManyCustomerInput = {
  compare?: InputMaybe<_CompareCustomerInput>;
  id: Scalars['ID']['input'];
};

export type _DeleteManyTaskCustomerAccessInput = {
  compare?: InputMaybe<_CompareTaskCustomerAccessInput>;
  id: Scalars['ID']['input'];
};

export type _DeleteManyTaskInput = {
  compare?: InputMaybe<_CompareTaskInput>;
  id: Scalars['ID']['input'];
};

export enum _DependsOnDependencyByGet {
  /** EXISTS */
  Exists = 'EXISTS',
  /** NOT_EXISTS */
  NotExists = 'NOT_EXISTS'
}

export enum _DependsOnDependencyByUpdateOrCreate {
  /** CREATED */
  Created = 'CREATED',
  /** NOT_CREATED */
  NotCreated = 'NOT_CREATED'
}

export type _Ec_Customer = {
  __typename?: '_EC_Customer';
  count: Scalars['Int']['output'];
  elems: Array<Customer>;
};

export type _Ec_RootDictionary = {
  __typename?: '_EC_RootDictionary';
  count: Scalars['Int']['output'];
  elems: Array<RootDictionary>;
};

export type _Ec_Stakeholder = {
  __typename?: '_EC_Stakeholder';
  count: Scalars['Int']['output'];
  elems: Array<Stakeholder>;
};

export type _Ec_Status = {
  __typename?: '_EC_Status';
  count: Scalars['Int']['output'];
  elems: Array<Status>;
};

export type _Ec_StatusGraph = {
  __typename?: '_EC_StatusGraph';
  count: Scalars['Int']['output'];
  elems: Array<StatusGraph>;
};

export type _Ec_Task = {
  __typename?: '_EC_Task';
  count: Scalars['Int']['output'];
  elems: Array<Task>;
};

export type _Ec_TaskCustomerAccess = {
  __typename?: '_EC_TaskCustomerAccess';
  count: Scalars['Int']['output'];
  elems: Array<TaskCustomerAccess>;
};

export type _Enc_AccessType = {
  __typename?: '_ENC_AccessType';
  count: Scalars['Int']['output'];
  elems: Array<_En_AccessType>;
};

export type _Enc_TaskStatus = {
  __typename?: '_ENC_TaskStatus';
  count: Scalars['Int']['output'];
  elems: Array<_En_TaskStatus>;
};

export type _Enc_TaskTag = {
  __typename?: '_ENC_TaskTag';
  count: Scalars['Int']['output'];
  elems: Array<_En_TaskTag>;
};

export enum _En_AccessType {
  Owner = 'OWNER',
  Read = 'READ',
  Write = 'WRITE'
}

export enum _En_TaskStatus {
  Done = 'DONE',
  Open = 'OPEN',
  Overdue = 'OVERDUE',
  Working = 'WORKING'
}

export enum _En_TaskTag {
  Critical = 'CRITICAL',
  HighPriority = 'HIGH_PRIORITY',
  LowPriority = 'LOW_PRIORITY',
  Usual = 'USUAL'
}

export type _E_Customer = Customer & _Entity & {
  __typename?: '_E_Customer';
  _calc: _Calculation;
  aggVersion: Scalars['Long']['output'];
  chgCnt?: Maybe<Scalars['Long']['output']>;
  email?: Maybe<Scalars['String']['output']>;
  id: Scalars['ID']['output'];
  lastChangeDate?: Maybe<Scalars['_DateTime']['output']>;
  login?: Maybe<Scalars['String']['output']>;
  ownerId?: Maybe<Scalars['String']['output']>;
  type: Scalars['String']['output'];
};

export type _E_RootDictionary = RootDictionary & _Entity & {
  __typename?: '_E_RootDictionary';
  _calc: _Calculation;
  id: Scalars['ID']['output'];
  type: Scalars['String']['output'];
};

export type _E_Stakeholder = Stakeholder & _Entity & {
  __typename?: '_E_Stakeholder';
  _calc: _Calculation;
  aggregateRoot?: Maybe<RootDictionary>;
  chgCnt?: Maybe<Scalars['Long']['output']>;
  code?: Maybe<Scalars['String']['output']>;
  id: Scalars['ID']['output'];
  lastChangeDate?: Maybe<Scalars['_DateTime']['output']>;
  name?: Maybe<Scalars['String']['output']>;
};


export type _E_StakeholderAggregateRootArgs = {
  alias?: InputMaybe<Scalars['String']['input']>;
};

export type _E_Status = Status & _Entity & {
  __typename?: '_E_Status';
  _calc: _Calculation;
  aggregateRoot?: Maybe<RootDictionary>;
  chgCnt?: Maybe<Scalars['Long']['output']>;
  code?: Maybe<Scalars['String']['output']>;
  description?: Maybe<Scalars['String']['output']>;
  id: Scalars['ID']['output'];
  initial?: Maybe<Scalars['Boolean']['output']>;
  lastChangeDate?: Maybe<Scalars['_DateTime']['output']>;
  name?: Maybe<Scalars['String']['output']>;
  stakeholder?: Maybe<Stakeholder>;
  statusType?: Maybe<Scalars['String']['output']>;
};


export type _E_StatusAggregateRootArgs = {
  alias?: InputMaybe<Scalars['String']['input']>;
};


export type _E_StatusStakeholderArgs = {
  alias?: InputMaybe<Scalars['String']['input']>;
};

export type _E_StatusGraph = StatusGraph & _Entity & {
  __typename?: '_E_StatusGraph';
  _calc: _Calculation;
  aggregateRoot?: Maybe<RootDictionary>;
  chgCnt?: Maybe<Scalars['Long']['output']>;
  code?: Maybe<Scalars['String']['output']>;
  id: Scalars['ID']['output'];
  label?: Maybe<Scalars['String']['output']>;
  lastChangeDate?: Maybe<Scalars['_DateTime']['output']>;
  name?: Maybe<Scalars['String']['output']>;
  statusFrom?: Maybe<Status>;
  statusTo?: Maybe<Status>;
};


export type _E_StatusGraphAggregateRootArgs = {
  alias?: InputMaybe<Scalars['String']['input']>;
};


export type _E_StatusGraphStatusFromArgs = {
  alias?: InputMaybe<Scalars['String']['input']>;
};


export type _E_StatusGraphStatusToArgs = {
  alias?: InputMaybe<Scalars['String']['input']>;
};

export type _E_Task = Task & _Entity & {
  __typename?: '_E_Task';
  _calc: _Calculation;
  accessList: _Ec_TaskCustomerAccess;
  aggVersion: Scalars['Long']['output'];
  chgCnt?: Maybe<Scalars['Long']['output']>;
  customer: _G_CustomerReference;
  description?: Maybe<Scalars['String']['output']>;
  dueDate?: Maybe<Scalars['_DateTime']['output']>;
  id: Scalars['ID']['output'];
  lastChangeDate?: Maybe<Scalars['_DateTime']['output']>;
  ownerId?: Maybe<Scalars['String']['output']>;
  status?: Maybe<_En_TaskStatus>;
  tags: _Enc_TaskTag;
  timeStamp?: Maybe<Scalars['_DateTime']['output']>;
  title?: Maybe<Scalars['String']['output']>;
  type: Scalars['String']['output'];
};


export type _E_TaskAccessListArgs = {
  cond?: InputMaybe<Scalars['String']['input']>;
  elemAlias?: InputMaybe<Scalars['String']['input']>;
  limit?: InputMaybe<Scalars['Int']['input']>;
  offset?: InputMaybe<Scalars['Int']['input']>;
  sort?: InputMaybe<Array<_SortCriterionSpecification>>;
};


export type _E_TaskTagsArgs = {
  cond?: InputMaybe<Scalars['String']['input']>;
  limit?: InputMaybe<Scalars['Int']['input']>;
  offset?: InputMaybe<Scalars['Int']['input']>;
  sort?: InputMaybe<Array<_SortCriterionSpecification>>;
};

export type _E_TaskCustomerAccess = TaskCustomerAccess & _Entity & {
  __typename?: '_E_TaskCustomerAccess';
  _calc: _Calculation;
  accessType?: Maybe<_En_AccessType>;
  aggVersion: Scalars['Long']['output'];
  aggregateRoot?: Maybe<Task>;
  chgCnt?: Maybe<Scalars['Long']['output']>;
  customer: _G_CustomerReference;
  id: Scalars['ID']['output'];
  lastChangeDate?: Maybe<Scalars['_DateTime']['output']>;
  ownerId?: Maybe<Scalars['String']['output']>;
  task: Task;
  type: Scalars['String']['output'];
};


export type _E_TaskCustomerAccessAggregateRootArgs = {
  alias?: InputMaybe<Scalars['String']['input']>;
};


export type _E_TaskCustomerAccessTaskArgs = {
  alias?: InputMaybe<Scalars['String']['input']>;
};

export type _Entity = {
  id: Scalars['ID']['output'];
};

export type _ExistCustomerInput = {
  compare?: InputMaybe<_CompareCustomerInput>;
  update?: InputMaybe<_ExistUpdateCustomerInput>;
};

export type _ExistUpdateCustomerInput = {
  _expr?: InputMaybe<Array<InputMaybe<_GenericExprInput>>>;
  email?: InputMaybe<Scalars['String']['input']>;
  login?: InputMaybe<Scalars['String']['input']>;
};

export type _G_CustomerReference = {
  __typename?: '_G_CustomerReference';
  entity?: Maybe<Customer>;
  entityId?: Maybe<Scalars['String']['output']>;
};


export type _G_CustomerReferenceEntityArgs = {
  alias?: InputMaybe<Scalars['String']['input']>;
};

export type _GenericExprInput = {
  expr: Scalars['String']['input'];
  fieldName: Scalars['String']['input'];
};

export enum _GetLockMode {
  /** NOT_USE */
  NotUse = 'NOT_USE',
  /** NOWAIT */
  Nowait = 'NOWAIT',
  /** WAIT */
  Wait = 'WAIT'
}

export type _MergedEntitiesCollection = {
  __typename?: '_MergedEntitiesCollection';
  count: Scalars['Int']['output'];
  elems: Array<_Entity>;
};

export type _Mutation = {
  __typename?: '_Mutation';
  packet?: Maybe<_Packet>;
};


export type _MutationPacketArgs = {
  aggregateVersion?: InputMaybe<Scalars['Long']['input']>;
  enableBuffering?: InputMaybe<Scalars['Boolean']['input']>;
  idempotencePacketId?: InputMaybe<Scalars['String']['input']>;
};

export type _Packet = {
  __typename?: '_Packet';
  _calc?: Maybe<_Calculation>;
  aggregateVersion?: Maybe<Scalars['Long']['output']>;
  createCustomer?: Maybe<Customer>;
  createManyCustomer?: Maybe<Array<Maybe<Scalars['String']['output']>>>;
  createManyTask?: Maybe<Array<Maybe<Scalars['String']['output']>>>;
  createManyTaskCustomerAccess?: Maybe<Array<Maybe<Scalars['String']['output']>>>;
  createTask?: Maybe<Task>;
  createTaskCustomerAccess?: Maybe<TaskCustomerAccess>;
  deleteCustomer?: Maybe<Scalars['String']['output']>;
  deleteManyCustomer?: Maybe<Scalars['String']['output']>;
  deleteManyTask?: Maybe<Scalars['String']['output']>;
  deleteManyTaskCustomerAccess?: Maybe<Scalars['String']['output']>;
  deleteTask?: Maybe<Scalars['String']['output']>;
  deleteTaskCustomerAccess?: Maybe<Scalars['String']['output']>;
  getCustomer?: Maybe<Customer>;
  getStakeholder?: Maybe<Stakeholder>;
  getStatus?: Maybe<Status>;
  getStatusGraph?: Maybe<StatusGraph>;
  getTask?: Maybe<Task>;
  getTaskCustomerAccess?: Maybe<TaskCustomerAccess>;
  isIdempotenceResponse?: Maybe<Scalars['Boolean']['output']>;
  updateCustomer?: Maybe<Customer>;
  updateManyCustomer?: Maybe<Scalars['String']['output']>;
  updateManyTask?: Maybe<Scalars['String']['output']>;
  updateManyTaskCustomerAccess?: Maybe<Scalars['String']['output']>;
  updateOrCreateCustomer?: Maybe<_UpdateOrCreateCustomerResponse>;
  updateOrCreateManyCustomer?: Maybe<Array<Maybe<_UpdateOrCreateManyResponse>>>;
  updateTask?: Maybe<Task>;
  updateTaskCustomerAccess?: Maybe<TaskCustomerAccess>;
};


export type _PacketCreateCustomerArgs = {
  input: _CreateCustomerInput;
};


export type _PacketCreateManyCustomerArgs = {
  input: Array<_CreateCustomerInput>;
};


export type _PacketCreateManyTaskArgs = {
  input: Array<_CreateTaskInput>;
};


export type _PacketCreateManyTaskCustomerAccessArgs = {
  input: Array<_CreateTaskCustomerAccessInput>;
};


export type _PacketCreateTaskArgs = {
  input: _CreateTaskInput;
};


export type _PacketCreateTaskCustomerAccessArgs = {
  input: _CreateTaskCustomerAccessInput;
};


export type _PacketDeleteCustomerArgs = {
  compare?: InputMaybe<_CompareCustomerInput>;
  id: Scalars['ID']['input'];
};


export type _PacketDeleteManyCustomerArgs = {
  input: Array<InputMaybe<_DeleteManyCustomerInput>>;
};


export type _PacketDeleteManyTaskArgs = {
  input: Array<InputMaybe<_DeleteManyTaskInput>>;
};


export type _PacketDeleteManyTaskCustomerAccessArgs = {
  input: Array<InputMaybe<_DeleteManyTaskCustomerAccessInput>>;
};


export type _PacketDeleteTaskArgs = {
  compare?: InputMaybe<_CompareTaskInput>;
  id: Scalars['ID']['input'];
};


export type _PacketDeleteTaskCustomerAccessArgs = {
  compare?: InputMaybe<_CompareTaskCustomerAccessInput>;
  id: Scalars['ID']['input'];
};


export type _PacketGetCustomerArgs = {
  failOnEmpty?: InputMaybe<Scalars['Boolean']['input']>;
  id: Scalars['ID']['input'];
  lock?: InputMaybe<_GetLockMode>;
  partCond?: InputMaybe<Scalars['String']['input']>;
};


export type _PacketGetStakeholderArgs = {
  failOnEmpty?: InputMaybe<Scalars['Boolean']['input']>;
  id: Scalars['ID']['input'];
  lock?: InputMaybe<_GetLockMode>;
  partCond?: InputMaybe<Scalars['String']['input']>;
};


export type _PacketGetStatusArgs = {
  failOnEmpty?: InputMaybe<Scalars['Boolean']['input']>;
  id: Scalars['ID']['input'];
  lock?: InputMaybe<_GetLockMode>;
  partCond?: InputMaybe<Scalars['String']['input']>;
};


export type _PacketGetStatusGraphArgs = {
  failOnEmpty?: InputMaybe<Scalars['Boolean']['input']>;
  id: Scalars['ID']['input'];
  lock?: InputMaybe<_GetLockMode>;
  partCond?: InputMaybe<Scalars['String']['input']>;
};


export type _PacketGetTaskArgs = {
  failOnEmpty?: InputMaybe<Scalars['Boolean']['input']>;
  id: Scalars['ID']['input'];
  lock?: InputMaybe<_GetLockMode>;
  partCond?: InputMaybe<Scalars['String']['input']>;
};


export type _PacketGetTaskCustomerAccessArgs = {
  failOnEmpty?: InputMaybe<Scalars['Boolean']['input']>;
  id: Scalars['ID']['input'];
  lock?: InputMaybe<_GetLockMode>;
  partCond?: InputMaybe<Scalars['String']['input']>;
};


export type _PacketUpdateCustomerArgs = {
  compare?: InputMaybe<_CompareCustomerInput>;
  input: _UpdateCustomerInput;
};


export type _PacketUpdateManyCustomerArgs = {
  input: Array<InputMaybe<_UpdateManyCustomerInput>>;
};


export type _PacketUpdateManyTaskArgs = {
  input: Array<InputMaybe<_UpdateManyTaskInput>>;
};


export type _PacketUpdateManyTaskCustomerAccessArgs = {
  input: Array<InputMaybe<_UpdateManyTaskCustomerAccessInput>>;
};


export type _PacketUpdateOrCreateCustomerArgs = {
  exist?: InputMaybe<_ExistCustomerInput>;
  input: _CreateCustomerInput;
};


export type _PacketUpdateOrCreateManyCustomerArgs = {
  input: Array<InputMaybe<_UpdateOrCreateManyCustomerInput>>;
};


export type _PacketUpdateTaskArgs = {
  compare?: InputMaybe<_CompareTaskInput>;
  input: _UpdateTaskInput;
};


export type _PacketUpdateTaskCustomerAccessArgs = {
  compare?: InputMaybe<_CompareTaskCustomerAccessInput>;
  input: _UpdateTaskCustomerAccessInput;
};

export type _Query = {
  __typename?: '_Query';
  merge: _MergedEntitiesCollection;
  resolveReferences: Array<_Reference>;
  searchCustomer: _Ec_Customer;
  searchRootDictionary: _Ec_RootDictionary;
  searchStakeholder: _Ec_Stakeholder;
  searchStatus: _Ec_Status;
  searchStatusGraph: _Ec_StatusGraph;
  searchTask: _Ec_Task;
  searchTaskCustomerAccess: _Ec_TaskCustomerAccess;
};


export type _QueryMergeArgs = {
  limit?: InputMaybe<Scalars['Int']['input']>;
  offset?: InputMaybe<Scalars['Int']['input']>;
  sort?: InputMaybe<Array<_SortCriterionSpecification>>;
};


export type _QueryResolveReferencesArgs = {
  ids: Array<Scalars['ID']['input']>;
  referenceType: Scalars['String']['input'];
};


export type _QuerySearchCustomerArgs = {
  cond?: InputMaybe<Scalars['String']['input']>;
  limit?: InputMaybe<Scalars['Int']['input']>;
  offset?: InputMaybe<Scalars['Int']['input']>;
  partCond?: InputMaybe<Scalars['String']['input']>;
  sort?: InputMaybe<Array<_SortCriterionSpecification>>;
};


export type _QuerySearchRootDictionaryArgs = {
  cond?: InputMaybe<Scalars['String']['input']>;
  limit?: InputMaybe<Scalars['Int']['input']>;
  offset?: InputMaybe<Scalars['Int']['input']>;
  partCond?: InputMaybe<Scalars['String']['input']>;
  sort?: InputMaybe<Array<_SortCriterionSpecification>>;
};


export type _QuerySearchStakeholderArgs = {
  cond?: InputMaybe<Scalars['String']['input']>;
  limit?: InputMaybe<Scalars['Int']['input']>;
  offset?: InputMaybe<Scalars['Int']['input']>;
  partCond?: InputMaybe<Scalars['String']['input']>;
  sort?: InputMaybe<Array<_SortCriterionSpecification>>;
};


export type _QuerySearchStatusArgs = {
  cond?: InputMaybe<Scalars['String']['input']>;
  limit?: InputMaybe<Scalars['Int']['input']>;
  offset?: InputMaybe<Scalars['Int']['input']>;
  partCond?: InputMaybe<Scalars['String']['input']>;
  sort?: InputMaybe<Array<_SortCriterionSpecification>>;
};


export type _QuerySearchStatusGraphArgs = {
  cond?: InputMaybe<Scalars['String']['input']>;
  limit?: InputMaybe<Scalars['Int']['input']>;
  offset?: InputMaybe<Scalars['Int']['input']>;
  partCond?: InputMaybe<Scalars['String']['input']>;
  sort?: InputMaybe<Array<_SortCriterionSpecification>>;
};


export type _QuerySearchTaskArgs = {
  cond?: InputMaybe<Scalars['String']['input']>;
  limit?: InputMaybe<Scalars['Int']['input']>;
  offset?: InputMaybe<Scalars['Int']['input']>;
  partCond?: InputMaybe<Scalars['String']['input']>;
  sort?: InputMaybe<Array<_SortCriterionSpecification>>;
};


export type _QuerySearchTaskCustomerAccessArgs = {
  cond?: InputMaybe<Scalars['String']['input']>;
  limit?: InputMaybe<Scalars['Int']['input']>;
  offset?: InputMaybe<Scalars['Int']['input']>;
  partCond?: InputMaybe<Scalars['String']['input']>;
  sort?: InputMaybe<Array<_SortCriterionSpecification>>;
};

export type _R_Customer = _Reference & {
  __typename?: '_R_Customer';
  entity?: Maybe<Customer>;
  entityId?: Maybe<Scalars['String']['output']>;
};

export type _R_RootDictionary = _Reference & {
  __typename?: '_R_RootDictionary';
  entity?: Maybe<RootDictionary>;
  entityId?: Maybe<Scalars['String']['output']>;
};

export type _R_Stakeholder = _Reference & {
  __typename?: '_R_Stakeholder';
  entity?: Maybe<Stakeholder>;
  entityId?: Maybe<Scalars['String']['output']>;
};

export type _R_Status = _Reference & {
  __typename?: '_R_Status';
  entity?: Maybe<Status>;
  entityId?: Maybe<Scalars['String']['output']>;
};

export type _R_StatusGraph = _Reference & {
  __typename?: '_R_StatusGraph';
  entity?: Maybe<StatusGraph>;
  entityId?: Maybe<Scalars['String']['output']>;
};

export type _R_Task = _Reference & {
  __typename?: '_R_Task';
  entity?: Maybe<Task>;
  entityId?: Maybe<Scalars['String']['output']>;
};

export type _R_TaskCustomerAccess = _Reference & {
  __typename?: '_R_TaskCustomerAccess';
  entity?: Maybe<TaskCustomerAccess>;
  entityId?: Maybe<Scalars['String']['output']>;
};

export type _Reference = {
  entityId?: Maybe<Scalars['String']['output']>;
};

export type _SingleReferenceInput = {
  entityId: Scalars['String']['input'];
};

export type _SortCriterionSpecification = {
  crit: Scalars['String']['input'];
  nullsLast?: InputMaybe<Scalars['Boolean']['input']>;
  order?: _SortOrder;
};

export enum _SortOrder {
  Asc = 'ASC',
  Desc = 'DESC'
}

export type _UpdateCustomerInput = {
  _expr?: InputMaybe<Array<InputMaybe<_GenericExprInput>>>;
  email?: InputMaybe<Scalars['String']['input']>;
  id: Scalars['ID']['input'];
  login?: InputMaybe<Scalars['String']['input']>;
};

export type _UpdateManyCustomerInput = {
  compare?: InputMaybe<_CompareCustomerInput>;
  param: _UpdateCustomerInput;
};

export type _UpdateManyTaskCustomerAccessInput = {
  compare?: InputMaybe<_CompareTaskCustomerAccessInput>;
  param: _UpdateTaskCustomerAccessInput;
};

export type _UpdateManyTaskInput = {
  compare?: InputMaybe<_CompareTaskInput>;
  param: _UpdateTaskInput;
};

export type _UpdateOrCreateCustomerResponse = {
  __typename?: '_UpdateOrCreateCustomerResponse';
  created?: Maybe<Scalars['Boolean']['output']>;
  returning?: Maybe<Customer>;
};

export type _UpdateOrCreateManyCustomerInput = {
  exist?: InputMaybe<_ExistCustomerInput>;
  param: _CreateCustomerInput;
};

export type _UpdateOrCreateManyResponse = {
  __typename?: '_UpdateOrCreateManyResponse';
  created?: Maybe<Scalars['Boolean']['output']>;
  id?: Maybe<Scalars['ID']['output']>;
};

export type _UpdateTaskCustomerAccessInput = {
  _expr?: InputMaybe<Array<InputMaybe<_GenericExprInput>>>;
  accessType?: InputMaybe<_En_AccessType>;
  customer?: InputMaybe<_SingleReferenceInput>;
  id: Scalars['ID']['input'];
  task?: InputMaybe<Scalars['ID']['input']>;
};

export type _UpdateTaskInput = {
  _expr?: InputMaybe<Array<InputMaybe<_GenericExprInput>>>;
  customer?: InputMaybe<_SingleReferenceInput>;
  description?: InputMaybe<Scalars['String']['input']>;
  dueDate?: InputMaybe<Scalars['_DateTime']['input']>;
  id: Scalars['ID']['input'];
  status?: InputMaybe<_En_TaskStatus>;
  tags?: InputMaybe<Array<InputMaybe<_En_TaskTag>>>;
  timeStamp?: InputMaybe<Scalars['_DateTime']['input']>;
  title?: InputMaybe<Scalars['String']['input']>;
};

export type CustomerAttributesFragment = { __typename?: '_E_Customer', id: string, login?: string | null, email?: string | null };

export type TaskCustomerAccessAttributesFragment = { __typename?: '_E_TaskCustomerAccess', id: string, accessType?: _En_AccessType | null, task: { __typename?: '_E_Task', id: string }, customer: { __typename?: '_G_CustomerReference', entity?: { __typename?: '_E_Customer', id: string, login?: string | null, email?: string | null } | null } };

export type TaskAttributesFragment = { __typename?: '_E_Task', id: string, title?: string | null, status?: _En_TaskStatus | null, dueDate?: any | null, description?: string | null, timeStamp?: any | null, tags: { __typename?: '_ENC_TaskTag', elems: Array<_En_TaskTag> }, accessList: { __typename?: '_EC_TaskCustomerAccess', elems: Array<{ __typename?: '_E_TaskCustomerAccess', id: string, accessType?: _En_AccessType | null, task: { __typename?: '_E_Task', id: string }, customer: { __typename?: '_G_CustomerReference', entity?: { __typename?: '_E_Customer', id: string, login?: string | null, email?: string | null } | null } }> }, customer: { __typename?: '_G_CustomerReference', entity?: { __typename?: '_E_Customer', id: string, login?: string | null, email?: string | null } | null } };

export type GetCustomerInfoQueryVariables = Exact<{
  cond: Scalars['String']['input'];
}>;


export type GetCustomerInfoQuery = { __typename?: '_Query', searchCustomer: { __typename?: '_EC_Customer', elems: Array<{ __typename?: '_E_Customer', id: string, login?: string | null, email?: string | null }> } };

export type AddCustomerInfoMutationVariables = Exact<{
  customerInput: _CreateCustomerInput;
  login: Scalars['String']['input'];
  email: Scalars['String']['input'];
}>;


export type AddCustomerInfoMutation = { __typename?: '_Mutation', packet?: { __typename?: '_Packet', updateOrCreateCustomer?: { __typename?: '_UpdateOrCreateCustomerResponse', returning?: { __typename?: '_E_Customer', id: string, login?: string | null, email?: string | null } | null } | null } | null };

export type SearchAllTaskQueryVariables = Exact<{
  cond?: InputMaybe<Scalars['String']['input']>;
}>;


export type SearchAllTaskQuery = { __typename?: '_Query', searchTask: { __typename?: '_EC_Task', elems: Array<{ __typename?: '_E_Task', id: string, title?: string | null, status?: _En_TaskStatus | null, dueDate?: any | null, description?: string | null, timeStamp?: any | null, tags: { __typename?: '_ENC_TaskTag', elems: Array<_En_TaskTag> }, accessList: { __typename?: '_EC_TaskCustomerAccess', elems: Array<{ __typename?: '_E_TaskCustomerAccess', id: string, accessType?: _En_AccessType | null, task: { __typename?: '_E_Task', id: string }, customer: { __typename?: '_G_CustomerReference', entity?: { __typename?: '_E_Customer', id: string, login?: string | null, email?: string | null } | null } }> }, customer: { __typename?: '_G_CustomerReference', entity?: { __typename?: '_E_Customer', id: string, login?: string | null, email?: string | null } | null } }> } };

export type CreateTaskCustomerAccessMutationVariables = Exact<{
  input: Array<_CreateTaskCustomerAccessInput> | _CreateTaskCustomerAccessInput;
}>;


export type CreateTaskCustomerAccessMutation = { __typename?: '_Mutation', packet?: { __typename?: '_Packet', createManyTaskCustomerAccess?: Array<string | null> | null } | null };

export type DeleteTaskCustomerAccessMutationVariables = Exact<{
  input: Array<_DeleteManyTaskCustomerAccessInput> | _DeleteManyTaskCustomerAccessInput;
}>;


export type DeleteTaskCustomerAccessMutation = { __typename?: '_Mutation', packet?: { __typename?: '_Packet', deleteManyTaskCustomerAccess?: string | null } | null };

export type CreateTaskMutationVariables = Exact<{
  input: _CreateTaskInput;
}>;


export type CreateTaskMutation = { __typename?: '_Mutation', packet?: { __typename?: '_Packet', createTask?: { __typename?: '_E_Task', id: string, title?: string | null, status?: _En_TaskStatus | null, dueDate?: any | null, description?: string | null, timeStamp?: any | null, customer: { __typename?: '_G_CustomerReference', entityId?: string | null, entity?: { __typename?: '_E_Customer', id: string, login?: string | null, email?: string | null } | null }, tags: { __typename?: '_ENC_TaskTag', elems: Array<_En_TaskTag> }, accessList: { __typename?: '_EC_TaskCustomerAccess', elems: Array<{ __typename?: '_E_TaskCustomerAccess', id: string, accessType?: _En_AccessType | null, task: { __typename?: '_E_Task', id: string }, customer: { __typename?: '_G_CustomerReference', entity?: { __typename?: '_E_Customer', id: string, login?: string | null, email?: string | null } | null } }> } } | null } | null };

export type UpdateTaskMutationVariables = Exact<{
  updateInput: _UpdateTaskInput;
}>;


export type UpdateTaskMutation = { __typename?: '_Mutation', packet?: { __typename?: '_Packet', updateTask?: { __typename?: '_E_Task', id: string, title?: string | null, status?: _En_TaskStatus | null, dueDate?: any | null, description?: string | null, timeStamp?: any | null, tags: { __typename?: '_ENC_TaskTag', elems: Array<_En_TaskTag> }, accessList: { __typename?: '_EC_TaskCustomerAccess', elems: Array<{ __typename?: '_E_TaskCustomerAccess', id: string, accessType?: _En_AccessType | null, task: { __typename?: '_E_Task', id: string }, customer: { __typename?: '_G_CustomerReference', entity?: { __typename?: '_E_Customer', id: string, login?: string | null, email?: string | null } | null } }> }, customer: { __typename?: '_G_CustomerReference', entity?: { __typename?: '_E_Customer', id: string, login?: string | null, email?: string | null } | null } } | null } | null };

export type DeleteTaskMutationVariables = Exact<{
  id: Scalars['ID']['input'];
}>;


export type DeleteTaskMutation = { __typename?: '_Mutation', packet?: { __typename?: '_Packet', deleteTask?: string | null } | null };

export const CustomerAttributesFragmentDoc = gql`
    fragment CustomerAttributes on _E_Customer {
  id
  login
  email
}
    `;
export const TaskCustomerAccessAttributesFragmentDoc = gql`
    fragment TaskCustomerAccessAttributes on TaskCustomerAccess {
  id
  task {
    id
  }
  customer {
    entity {
      ...CustomerAttributes
    }
  }
  accessType
}
    ${CustomerAttributesFragmentDoc}`;
export const TaskAttributesFragmentDoc = gql`
    fragment TaskAttributes on _E_Task {
  id
  title
  status
  dueDate
  description
  timeStamp
  tags {
    elems
  }
  accessList {
    elems {
      ...TaskCustomerAccessAttributes
    }
  }
  customer {
    entity {
      ...CustomerAttributes
    }
  }
}
    ${TaskCustomerAccessAttributesFragmentDoc}
${CustomerAttributesFragmentDoc}`;
export const GetCustomerInfoDocument = gql`
    query getCustomerInfo($cond: String!) {
  searchCustomer(cond: $cond) {
    elems {
      ...CustomerAttributes
    }
  }
}
    ${CustomerAttributesFragmentDoc}`;

/**
 * __useGetCustomerInfoQuery__
 *
 * To run a query within a React component, call `useGetCustomerInfoQuery` and pass it any options that fit your needs.
 * When your component renders, `useGetCustomerInfoQuery` returns an object from Apollo Client that contains loading, error, and data properties
 * you can use to render your UI.
 *
 * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
 *
 * @example
 * const { data, loading, error } = useGetCustomerInfoQuery({
 *   variables: {
 *      cond: // value for 'cond'
 *   },
 * });
 */
export function useGetCustomerInfoQuery(baseOptions: Apollo.QueryHookOptions<GetCustomerInfoQuery, GetCustomerInfoQueryVariables> & ({ variables: GetCustomerInfoQueryVariables; skip?: boolean; } | { skip: boolean; }) ) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useQuery<GetCustomerInfoQuery, GetCustomerInfoQueryVariables>(GetCustomerInfoDocument, options);
      }
export function useGetCustomerInfoLazyQuery(baseOptions?: Apollo.LazyQueryHookOptions<GetCustomerInfoQuery, GetCustomerInfoQueryVariables>) {
          const options = {...defaultOptions, ...baseOptions}
          return Apollo.useLazyQuery<GetCustomerInfoQuery, GetCustomerInfoQueryVariables>(GetCustomerInfoDocument, options);
        }
export function useGetCustomerInfoSuspenseQuery(baseOptions?: Apollo.SkipToken | Apollo.SuspenseQueryHookOptions<GetCustomerInfoQuery, GetCustomerInfoQueryVariables>) {
          const options = baseOptions === Apollo.skipToken ? baseOptions : {...defaultOptions, ...baseOptions}
          return Apollo.useSuspenseQuery<GetCustomerInfoQuery, GetCustomerInfoQueryVariables>(GetCustomerInfoDocument, options);
        }
export type GetCustomerInfoQueryHookResult = ReturnType<typeof useGetCustomerInfoQuery>;
export type GetCustomerInfoLazyQueryHookResult = ReturnType<typeof useGetCustomerInfoLazyQuery>;
export type GetCustomerInfoSuspenseQueryHookResult = ReturnType<typeof useGetCustomerInfoSuspenseQuery>;
export type GetCustomerInfoQueryResult = Apollo.QueryResult<GetCustomerInfoQuery, GetCustomerInfoQueryVariables>;
export const AddCustomerInfoDocument = gql`
    mutation addCustomerInfo($customerInput: _CreateCustomerInput!, $login: String!, $email: String!) {
  packet {
    updateOrCreateCustomer(
      input: $customerInput
      exist: {update: {login: $login, email: $email}}
    ) {
      returning {
        ...CustomerAttributes
      }
    }
  }
}
    ${CustomerAttributesFragmentDoc}`;
export type AddCustomerInfoMutationFn = Apollo.MutationFunction<AddCustomerInfoMutation, AddCustomerInfoMutationVariables>;

/**
 * __useAddCustomerInfoMutation__
 *
 * To run a mutation, you first call `useAddCustomerInfoMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useAddCustomerInfoMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [addCustomerInfoMutation, { data, loading, error }] = useAddCustomerInfoMutation({
 *   variables: {
 *      customerInput: // value for 'customerInput'
 *      login: // value for 'login'
 *      email: // value for 'email'
 *   },
 * });
 */
export function useAddCustomerInfoMutation(baseOptions?: Apollo.MutationHookOptions<AddCustomerInfoMutation, AddCustomerInfoMutationVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useMutation<AddCustomerInfoMutation, AddCustomerInfoMutationVariables>(AddCustomerInfoDocument, options);
      }
export type AddCustomerInfoMutationHookResult = ReturnType<typeof useAddCustomerInfoMutation>;
export type AddCustomerInfoMutationResult = Apollo.MutationResult<AddCustomerInfoMutation>;
export type AddCustomerInfoMutationOptions = Apollo.BaseMutationOptions<AddCustomerInfoMutation, AddCustomerInfoMutationVariables>;
export const SearchAllTaskDocument = gql`
    query searchAllTask($cond: String) {
  searchTask(cond: $cond, sort: {crit: "it.timeStamp", order: DESC}) {
    elems {
      ...TaskAttributes
    }
  }
}
    ${TaskAttributesFragmentDoc}`;

/**
 * __useSearchAllTaskQuery__
 *
 * To run a query within a React component, call `useSearchAllTaskQuery` and pass it any options that fit your needs.
 * When your component renders, `useSearchAllTaskQuery` returns an object from Apollo Client that contains loading, error, and data properties
 * you can use to render your UI.
 *
 * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
 *
 * @example
 * const { data, loading, error } = useSearchAllTaskQuery({
 *   variables: {
 *      cond: // value for 'cond'
 *   },
 * });
 */
export function useSearchAllTaskQuery(baseOptions?: Apollo.QueryHookOptions<SearchAllTaskQuery, SearchAllTaskQueryVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useQuery<SearchAllTaskQuery, SearchAllTaskQueryVariables>(SearchAllTaskDocument, options);
      }
export function useSearchAllTaskLazyQuery(baseOptions?: Apollo.LazyQueryHookOptions<SearchAllTaskQuery, SearchAllTaskQueryVariables>) {
          const options = {...defaultOptions, ...baseOptions}
          return Apollo.useLazyQuery<SearchAllTaskQuery, SearchAllTaskQueryVariables>(SearchAllTaskDocument, options);
        }
export function useSearchAllTaskSuspenseQuery(baseOptions?: Apollo.SkipToken | Apollo.SuspenseQueryHookOptions<SearchAllTaskQuery, SearchAllTaskQueryVariables>) {
          const options = baseOptions === Apollo.skipToken ? baseOptions : {...defaultOptions, ...baseOptions}
          return Apollo.useSuspenseQuery<SearchAllTaskQuery, SearchAllTaskQueryVariables>(SearchAllTaskDocument, options);
        }
export type SearchAllTaskQueryHookResult = ReturnType<typeof useSearchAllTaskQuery>;
export type SearchAllTaskLazyQueryHookResult = ReturnType<typeof useSearchAllTaskLazyQuery>;
export type SearchAllTaskSuspenseQueryHookResult = ReturnType<typeof useSearchAllTaskSuspenseQuery>;
export type SearchAllTaskQueryResult = Apollo.QueryResult<SearchAllTaskQuery, SearchAllTaskQueryVariables>;
export const CreateTaskCustomerAccessDocument = gql`
    mutation createTaskCustomerAccess($input: [_CreateTaskCustomerAccessInput!]!) {
  packet {
    createManyTaskCustomerAccess(input: $input)
  }
}
    `;
export type CreateTaskCustomerAccessMutationFn = Apollo.MutationFunction<CreateTaskCustomerAccessMutation, CreateTaskCustomerAccessMutationVariables>;

/**
 * __useCreateTaskCustomerAccessMutation__
 *
 * To run a mutation, you first call `useCreateTaskCustomerAccessMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useCreateTaskCustomerAccessMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [createTaskCustomerAccessMutation, { data, loading, error }] = useCreateTaskCustomerAccessMutation({
 *   variables: {
 *      input: // value for 'input'
 *   },
 * });
 */
export function useCreateTaskCustomerAccessMutation(baseOptions?: Apollo.MutationHookOptions<CreateTaskCustomerAccessMutation, CreateTaskCustomerAccessMutationVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useMutation<CreateTaskCustomerAccessMutation, CreateTaskCustomerAccessMutationVariables>(CreateTaskCustomerAccessDocument, options);
      }
export type CreateTaskCustomerAccessMutationHookResult = ReturnType<typeof useCreateTaskCustomerAccessMutation>;
export type CreateTaskCustomerAccessMutationResult = Apollo.MutationResult<CreateTaskCustomerAccessMutation>;
export type CreateTaskCustomerAccessMutationOptions = Apollo.BaseMutationOptions<CreateTaskCustomerAccessMutation, CreateTaskCustomerAccessMutationVariables>;
export const DeleteTaskCustomerAccessDocument = gql`
    mutation deleteTaskCustomerAccess($input: [_DeleteManyTaskCustomerAccessInput!]!) {
  packet {
    deleteManyTaskCustomerAccess(input: $input)
  }
}
    `;
export type DeleteTaskCustomerAccessMutationFn = Apollo.MutationFunction<DeleteTaskCustomerAccessMutation, DeleteTaskCustomerAccessMutationVariables>;

/**
 * __useDeleteTaskCustomerAccessMutation__
 *
 * To run a mutation, you first call `useDeleteTaskCustomerAccessMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useDeleteTaskCustomerAccessMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [deleteTaskCustomerAccessMutation, { data, loading, error }] = useDeleteTaskCustomerAccessMutation({
 *   variables: {
 *      input: // value for 'input'
 *   },
 * });
 */
export function useDeleteTaskCustomerAccessMutation(baseOptions?: Apollo.MutationHookOptions<DeleteTaskCustomerAccessMutation, DeleteTaskCustomerAccessMutationVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useMutation<DeleteTaskCustomerAccessMutation, DeleteTaskCustomerAccessMutationVariables>(DeleteTaskCustomerAccessDocument, options);
      }
export type DeleteTaskCustomerAccessMutationHookResult = ReturnType<typeof useDeleteTaskCustomerAccessMutation>;
export type DeleteTaskCustomerAccessMutationResult = Apollo.MutationResult<DeleteTaskCustomerAccessMutation>;
export type DeleteTaskCustomerAccessMutationOptions = Apollo.BaseMutationOptions<DeleteTaskCustomerAccessMutation, DeleteTaskCustomerAccessMutationVariables>;
export const CreateTaskDocument = gql`
    mutation createTask($input: _CreateTaskInput!) {
  packet {
    createTask(input: $input) {
      customer {
        entityId
      }
      ...TaskAttributes
    }
  }
}
    ${TaskAttributesFragmentDoc}`;
export type CreateTaskMutationFn = Apollo.MutationFunction<CreateTaskMutation, CreateTaskMutationVariables>;

/**
 * __useCreateTaskMutation__
 *
 * To run a mutation, you first call `useCreateTaskMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useCreateTaskMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [createTaskMutation, { data, loading, error }] = useCreateTaskMutation({
 *   variables: {
 *      input: // value for 'input'
 *   },
 * });
 */
export function useCreateTaskMutation(baseOptions?: Apollo.MutationHookOptions<CreateTaskMutation, CreateTaskMutationVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useMutation<CreateTaskMutation, CreateTaskMutationVariables>(CreateTaskDocument, options);
      }
export type CreateTaskMutationHookResult = ReturnType<typeof useCreateTaskMutation>;
export type CreateTaskMutationResult = Apollo.MutationResult<CreateTaskMutation>;
export type CreateTaskMutationOptions = Apollo.BaseMutationOptions<CreateTaskMutation, CreateTaskMutationVariables>;
export const UpdateTaskDocument = gql`
    mutation updateTask($updateInput: _UpdateTaskInput!) {
  packet {
    updateTask(input: $updateInput) {
      ...TaskAttributes
    }
  }
}
    ${TaskAttributesFragmentDoc}`;
export type UpdateTaskMutationFn = Apollo.MutationFunction<UpdateTaskMutation, UpdateTaskMutationVariables>;

/**
 * __useUpdateTaskMutation__
 *
 * To run a mutation, you first call `useUpdateTaskMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useUpdateTaskMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [updateTaskMutation, { data, loading, error }] = useUpdateTaskMutation({
 *   variables: {
 *      updateInput: // value for 'updateInput'
 *   },
 * });
 */
export function useUpdateTaskMutation(baseOptions?: Apollo.MutationHookOptions<UpdateTaskMutation, UpdateTaskMutationVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useMutation<UpdateTaskMutation, UpdateTaskMutationVariables>(UpdateTaskDocument, options);
      }
export type UpdateTaskMutationHookResult = ReturnType<typeof useUpdateTaskMutation>;
export type UpdateTaskMutationResult = Apollo.MutationResult<UpdateTaskMutation>;
export type UpdateTaskMutationOptions = Apollo.BaseMutationOptions<UpdateTaskMutation, UpdateTaskMutationVariables>;
export const DeleteTaskDocument = gql`
    mutation deleteTask($id: ID!) {
  packet {
    deleteTask(id: $id)
  }
}
    `;
export type DeleteTaskMutationFn = Apollo.MutationFunction<DeleteTaskMutation, DeleteTaskMutationVariables>;

/**
 * __useDeleteTaskMutation__
 *
 * To run a mutation, you first call `useDeleteTaskMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useDeleteTaskMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [deleteTaskMutation, { data, loading, error }] = useDeleteTaskMutation({
 *   variables: {
 *      id: // value for 'id'
 *   },
 * });
 */
export function useDeleteTaskMutation(baseOptions?: Apollo.MutationHookOptions<DeleteTaskMutation, DeleteTaskMutationVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useMutation<DeleteTaskMutation, DeleteTaskMutationVariables>(DeleteTaskDocument, options);
      }
export type DeleteTaskMutationHookResult = ReturnType<typeof useDeleteTaskMutation>;
export type DeleteTaskMutationResult = Apollo.MutationResult<DeleteTaskMutation>;
export type DeleteTaskMutationOptions = Apollo.BaseMutationOptions<DeleteTaskMutation, DeleteTaskMutationVariables>;