import { DocumentNode } from 'graphql'

export type DocumentNodeList = Array<{ document: DocumentNode }>
export const entityPrefix = "_E_"
export const entityRefCollectionPrefix = "_E_Rci"
export const entityCollectionPrefix = "_EC_"
export const enumPrefix = "_EN_"
export const enumCollectionPrefix = "_ENC_"
export const referencePrefix = "_G_"
export const referencePostfix = "Reference"
export const referenceFields = ["entityId", "entity"]
export const statusHistoryFieldName = "statusHistory"
export const aggregateRootFieldName = "aggregateRoot"
export const eventSpecificFieldNameList = ["currentAutInfo", "creationTimestamp"]
export const statusHistoryFieldPostfix = "HObjectStatus"
export const createInputTypePrefix = "_Create"
export const deleteManyInputTypePrefix = "_DeleteMany"
export const inputTypePostfix = "Input"
export const primitiveTypeList = ["String", "Int", "Float", "Boolean", "ID", "BigDecimal", "_Date", "_DateTime"]
export const rootDictionaryTypeName = "RootDictionary"


export const statusTypeName = "Status"
export const systemTypesList = [
    rootDictionaryTypeName, "Stakeholder", "Status", "StatusGraph", "SysAdminSettings"
    , "SysOperation", "SysCheckSelect", "SysParamAddition", "SysRootSecurity", "SysRequestControl"
].map(t => entityPrefix.concat(t))
export const mandatoryFieldList = ['id', '__typename']
export const systemFieldList = [
    "_calc", "aggVersion", "chgCnt", "lastChangeDate", "ownerId"
    , "type", "SysCheckSelect", "SysParamAddition", "SysRootSecurity", "sysIdPrefix"
]
export const historySystemFields = ["sysChangeUser", "sysStatusUpdated"]

export const generatedFragmentPostfix = "Attributes"

export const searchPrefix = "search"
export const createPrefix = "create"
export const updatePrefix = "update"
export const deletePrefix = "delete"
export const updateOrCreatePrefix = "updateOrCreate"

export const getPrefix = "get"
