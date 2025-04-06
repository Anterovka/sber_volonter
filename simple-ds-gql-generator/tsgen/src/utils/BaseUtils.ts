import { DefinitionNode, DocumentNode, FieldDefinitionNode, InputObjectTypeDefinitionNode, ObjectTypeDefinitionNode } from "graphql";
import { entityPrefix, referencePrefix, systemTypesList, systemFieldList, historySystemFields, statusHistoryFieldPostfix, inputTypePostfix, createInputTypePrefix, aggregateRootFieldName, rootDictionaryTypeName, referenceFields, eventSpecificFieldNameList, entityRefCollectionPrefix } from './Constants'

export const toLowerCaseFirstLetter = (str: string): string => {
    return str.charAt(0).toLowerCase() + str.slice(1)
}
export const toUpperCaseFirstLetter = (str: string): string => {
    return str.charAt(0).toUpperCase() + str.slice(1)
}



export const isDictionaryDefinition = (node: ObjectTypeDefinitionNode): boolean => {
    return !!(node.fields?.find(f =>
        f.name.value === aggregateRootFieldName && f.type.kind === "NamedType" && f.type.name.value === rootDictionaryTypeName)
    )
}

const extractAggregateRootField = (node: ObjectTypeDefinitionNode): FieldDefinitionNode | undefined => {
    return node.fields?.find(f => f.name.value === aggregateRootFieldName)
}

export const isAggregateRootDefinition = (node: ObjectTypeDefinitionNode): Boolean => {
    return !extractAggregateRootField(node)
}

const isBusinessObjectTypeDefinition = (dn: DefinitionNode): dn is ObjectTypeDefinitionNode => {
    return dn.kind === "ObjectTypeDefinition"
        && dn.name.value.startsWith(entityPrefix)
        && !(dn.name.value.startsWith(entityRefCollectionPrefix))
        && !(systemTypesList.includes(dn.name.value))
        && !(dn.name.value.endsWith(statusHistoryFieldPostfix))
        && !(dn.fields?.find(f => historySystemFields.includes(f.name.value)))
        && !(dn.fields?.find(f => eventSpecificFieldNameList.includes(f.name.value)))
}
export const extractBusinessObjectTypeNodeList = (astNode: DocumentNode): readonly ObjectTypeDefinitionNode[] => {
    return astNode.definitions.filter(isBusinessObjectTypeDefinition)
}

const isInputObjectTypeDefinition = (dn: DefinitionNode): dn is InputObjectTypeDefinitionNode => {
    return dn.kind === "InputObjectTypeDefinition"
}
export const extractInputObjectTypeNodeList = (astNode: DocumentNode): readonly InputObjectTypeDefinitionNode[] => {
    return astNode.definitions.filter(isInputObjectTypeDefinition)
}
export const extractInputObjectTypeForObjectType = (
    inputObjectTypeNodeList: readonly InputObjectTypeDefinitionNode[],
    objectTypeNode: ObjectTypeDefinitionNode,
    inputPrefix?: string
): InputObjectTypeDefinitionNode | undefined => {

    const prefixedNodeName = objectTypeNode.name.value
    const nodeName = prefixedNodeName.substring(entityPrefix.length)

    return inputObjectTypeNodeList.find(n => n.name.value === `${inputPrefix ?? createInputTypePrefix}${nodeName}${inputTypePostfix}`)
}

export const extractNodeBusinessFieldList = (node: ObjectTypeDefinitionNode): readonly FieldDefinitionNode[] => {
    return node.fields?.filter(f => !(systemFieldList.includes(f.name.value))) ?? []
}

export const extractEntityName = (name: string): string => {
    if (name.startsWith(entityPrefix))
     return name.substring(entityPrefix.length)

    return name    
}

export const extractNodeAggregateName = (node: ObjectTypeDefinitionNode): string => {

    if (isAggregateRootDefinition(node))
        return node.name.value

    const aggregateRootField = extractAggregateRootField(node)

    return aggregateRootField?.type.kind === "NamedType" ?
        aggregateRootField.type.name.value :
        ""
}



const isEmbeddedObjectTypeDefinition = (dn: DefinitionNode): dn is ObjectTypeDefinitionNode => {
    return dn.kind === "ObjectTypeDefinition"
        && dn.name.value.startsWith(referencePrefix)
        && dn.fields ?
        dn.fields.map(f => f.name.value).filter(fn => !referenceFields.includes(fn)).length > 0
        : false
}
export const extractEmbeddedObjectTypeNodeList = (astNode: DocumentNode): readonly ObjectTypeDefinitionNode[] => {
    return astNode.definitions.filter(isEmbeddedObjectTypeDefinition)
}






