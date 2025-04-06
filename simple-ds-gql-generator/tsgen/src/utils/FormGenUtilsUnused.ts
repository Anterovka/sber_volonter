import { DocumentNode, FieldDefinitionNode, InputObjectTypeDefinitionNode, ObjectTypeDefinitionNode } from "graphql";
import { entityPrefix, entityCollectionPrefix, enumCollectionPrefix, primitiveTypeList, referencePrefix, referencePostfix, statusHistoryFieldName, generatedFragmentPostfix, inputTypePostfix, createInputTypePrefix, aggregateRootFieldName, enumPrefix, statusTypeName } from './Constants'
import { extractNodeBusinessFieldList } from "./BaseUtils"
import graphql from 'graphql-tag'

const toLowerCaseFirstLetter = (str: string): string => {
    return str.charAt(0).toLowerCase() + str.slice(1)
}


const getInnerReferenceFragmentField = (
    fdn: FieldDefinitionNode,
    businessObjectTypeNodeList: readonly ObjectTypeDefinitionNode[],
    embeddedObjectTypeNodeList: readonly ObjectTypeDefinitionNode[],
    simpleMode: boolean
): string | undefined => {

    if (fdn.type.kind === "NonNullType"
        && fdn.type.type.kind === "NamedType"
    ) {

        const innerReferenceName = fdn.type.type.name.value

        const businessObjectTypeNode =
            businessObjectTypeNodeList.find(n => entityPrefix.concat(innerReferenceName) === n.name.value)

        if (businessObjectTypeNode) {
            if ((!simpleMode)) {

                const businessFieldList = extractNodeBusinessFieldList(businessObjectTypeNode)
                const businessFieldListStr = getFragmentFieldList(
                    businessFieldList,
                    businessObjectTypeNodeList,
                    embeddedObjectTypeNodeList,
                    true
                )

                return `
                ${fdn.name.value} {
                      ${businessFieldListStr}
                }
                `
            } else {
                return `
                ${fdn.name.value} {
                  id
                }
                `
            }
        }
    }
}


const getChildListFragmentField = (
    fdn: FieldDefinitionNode,
    businessObjectTypeNodeList: readonly ObjectTypeDefinitionNode[],
    embeddedObjectTypeNodeList: readonly ObjectTypeDefinitionNode[],
    simpleMode: boolean
): string | undefined => {

    if (fdn.type.kind === "NonNullType"
        && fdn.type.type.kind === "NamedType"
        && fdn.type.type.name.value.startsWith(entityCollectionPrefix)
        && fdn.name.value !== statusHistoryFieldName
    ) {
        const entityName = fdn.type.type.name.value.substring(entityCollectionPrefix.length)

        const businessObjectTypeNode =
            businessObjectTypeNodeList.find(n => entityPrefix.concat(entityName) === n.name.value)

        if (!simpleMode && businessObjectTypeNode) {
            const businessFieldList = extractNodeBusinessFieldList(businessObjectTypeNode)
            const businessFieldListStr = getFragmentFieldList(
                businessFieldList,
                businessObjectTypeNodeList,
                embeddedObjectTypeNodeList,
                true
            )

            return `
              ${fdn.name.value} {
                elems {
                  ${businessFieldListStr}
                }
              }
            `
        }
    }
}
const getEnumFragmentField = (fdn: FieldDefinitionNode): string | undefined => {

    const fieldNodeType = fdn.type.kind === "NonNullType" ? fdn.type.type : fdn.type

    if (fieldNodeType.kind === "NamedType"
        && fieldNodeType.name.value.startsWith(enumPrefix))
        return `${fdn.name.value}`
}

const getEnumCollectionFragmentField = (fdn: FieldDefinitionNode): string | undefined => {

    if (fdn.type.kind === "NonNullType"
        && fdn.type.type.kind === "NamedType"
        && fdn.type.type.name.value.startsWith(enumCollectionPrefix))
        return `
        ${fdn.name.value} {
            elems 
          }
        `
}
const getReferenceFragmentField = (
    fdn: FieldDefinitionNode,
    businessObjectTypeNodeList: readonly ObjectTypeDefinitionNode[],
    embeddedObjectTypeNodeList: readonly ObjectTypeDefinitionNode[],
    simpleMode: boolean
): string | undefined => {

    if (fdn.type.kind === "NonNullType"
        && fdn.type.type.kind === "NamedType"
        && fdn.type.type.name.value.startsWith(referencePrefix)
        && fdn.type.type.name.value.endsWith(referencePostfix)
    ) {
        const referenceName = fdn.type.type.name.value.substring(referencePrefix.length).slice(0, -referencePostfix.length)

        const businessObjectTypeNode =
            businessObjectTypeNodeList.find(n => entityPrefix.concat(referenceName) === n.name.value)

        if ((!simpleMode) && businessObjectTypeNode) {

            const businessFieldList = extractNodeBusinessFieldList(businessObjectTypeNode)
            const businessFieldListStr = getFragmentFieldList(
                businessFieldList,
                businessObjectTypeNodeList,
                embeddedObjectTypeNodeList,
                true
            )

            return `
                ${fdn.name.value} {
                    entity {
                      ${businessFieldListStr}
                    }
                }
                `
        } else {
            return `
            ${fdn.name.value} {
              entityId
            }
            `
        }
    }
}

const getEmbeddedFragmentField = (fdn: FieldDefinitionNode, embeddedObjectTypeNodeList: readonly ObjectTypeDefinitionNode[]): string | undefined => {

    if (fdn.type.kind === "NonNullType"
        && fdn.type.type.kind === "NamedType"
        && fdn.type.type.name.value.startsWith(referencePrefix)
    ) {

        const fieldTypeName = fdn.type.type.name.value

        const embeddedObjectTypeNode = embeddedObjectTypeNodeList.find(n => n.name.value === fieldTypeName)

        if (embeddedObjectTypeNode
            && embeddedObjectTypeNode.fields
            && embeddedObjectTypeNode.fields.length > 0) {
            const embeddedObjectFieldNameList = embeddedObjectTypeNode.fields.map(f => f.name.value)

            return `
                    ${fdn.name.value} {
                        ${embeddedObjectFieldNameList.join("\n")}
                    }
            `

        }
    }
}

const getStatusFragmentField = (fdn: FieldDefinitionNode): string | undefined => {

    if (fdn.type.kind === "NamedType" && fdn.type.name.value === statusTypeName) {
        return `
        ${fdn.name.value}{code}
        `
    }
}

const getPrimitiveFragmentField = (fdn: FieldDefinitionNode): string | undefined => {

    const fieldNodeType = fdn.type.kind === "NonNullType" ? fdn.type.type : fdn.type

    if (fieldNodeType.kind === "NamedType"
        && primitiveTypeList.includes(fieldNodeType.name.value))

        return `
        ${fdn.name.value}
        `
}

const getFragmentFieldList = (
    businessFieldList: readonly FieldDefinitionNode[],
    businessObjectTypeNodeList: readonly ObjectTypeDefinitionNode[],
    embeddedObjectTypeNodeList: readonly ObjectTypeDefinitionNode[],
    simpleMode: boolean

): string => {
    const businessFieldListStr = businessFieldList.map(f => {
        return getInnerReferenceFragmentField(f, businessObjectTypeNodeList, embeddedObjectTypeNodeList, simpleMode)
            ?? getChildListFragmentField(f, businessObjectTypeNodeList, embeddedObjectTypeNodeList, simpleMode)
            ?? getEnumFragmentField(f)
            ?? getEnumCollectionFragmentField(f)
            ?? getReferenceFragmentField(f, businessObjectTypeNodeList, embeddedObjectTypeNodeList, simpleMode)
            ?? getEmbeddedFragmentField(f, embeddedObjectTypeNodeList)
            ?? getStatusFragmentField(f)
            ?? getPrimitiveFragmentField(f)
            ?? ""
    }).join('\n')

    return businessFieldListStr

}
export const getFragment = (
    node: ObjectTypeDefinitionNode,
    businessObjectTypeNodeList: readonly ObjectTypeDefinitionNode[],
    embeddedObjectTypeNodeList: readonly ObjectTypeDefinitionNode[],
    simpleMode: boolean
): DocumentNode => {

    const basicFieldNameList = ['id', '__typename']

    const businessFieldListStr = basicFieldNameList.join("\n").concat(
        getFragmentFieldList(
            extractNodeBusinessFieldList(node).filter(n => !basicFieldNameList.includes(n.name.value)),
            businessObjectTypeNodeList,
            embeddedObjectTypeNodeList,
            simpleMode
        )
    )
    const prefixedNodeName = node.name.value
    const nodeName = prefixedNodeName.substring(entityPrefix.length)

    return graphql`fragment ${nodeName}${generatedFragmentPostfix} on ${prefixedNodeName} {\n${businessFieldListStr}}`

}

export const getSearchQuery = (node: ObjectTypeDefinitionNode): DocumentNode => {

    const prefixedNodeName = node.name.value
    const nodeName = prefixedNodeName.substring(entityPrefix.length)

    return graphql`query search${nodeName}($cond: String){ search${nodeName}(cond: $cond){elems{...${nodeName}${generatedFragmentPostfix}}}}`
}

export const getCreateMutation = (node: ObjectTypeDefinitionNode): DocumentNode => {

    const prefixedNodeName = node.name.value
    const nodeName = prefixedNodeName.substring(entityPrefix.length)

    return graphql`
        mutation create${nodeName}($input: _Create${nodeName}Input!){
            packet{
                create${nodeName}(input:$input){
                    ...${nodeName}${generatedFragmentPostfix}
                }
            }
        }`
}

export const getUpdateMutation = (node: ObjectTypeDefinitionNode): DocumentNode => {

    const prefixedNodeName = node.name.value
    const nodeName = prefixedNodeName.substring(entityPrefix.length)

    return graphql`
        mutation update${nodeName}($input: _Update${nodeName}Input!){
            packet{
                update${nodeName}(input:$input){
                    ...${nodeName}${generatedFragmentPostfix}
                }
            }
        }`
}

export const getDeleteMutation = (node: ObjectTypeDefinitionNode): DocumentNode => {

    const prefixedNodeName = node.name.value
    const nodeName = prefixedNodeName.substring(entityPrefix.length)

    return graphql`
        mutation delete${nodeName}($id: ID!){
            packet{
                delete${nodeName}(id:$id)
            }
        }`
}

export const getUpdateOrCreateMutation = (inputNode: InputObjectTypeDefinitionNode, isDictionary: boolean): DocumentNode | undefined => {

    if (!(inputNode.fields?.find(f => f.name.value === "id" && f.type.kind === "NonNullType"))) {
        return
    }

    const inputNodeName = inputNode.name.value
    const prefixedNodeName = inputNodeName.substring(0, inputNodeName.length - inputTypePostfix.length)
    const nodeName = prefixedNodeName.substring(createInputTypePrefix.length)

    const packetName = isDictionary ? "dictionaryPacket" : "packet"

    return graphql`
        mutation updateOrCreate${nodeName}($input: _Create${nodeName}Input!){
            ${packetName}{
                updateOrCreate${nodeName}(input:$input){
                    returning{
                        ...${nodeName}${generatedFragmentPostfix}
                    }
                }
            }
        }`
}

export const getCreateManyMutation = (node: ObjectTypeDefinitionNode): DocumentNode | undefined => {

    if (!(node.fields?.find(f => f.name.value === aggregateRootFieldName))) {
        return
    }

    const prefixedNodeName = node.name.value
    const nodeName = prefixedNodeName.substring(entityPrefix.length)

    return graphql`
        mutation createMany${nodeName}($input: [_Create${nodeName}Input!]!){
            packet{
                createMany${nodeName}(input:$input)
            }
        }`
}

export const getDeleteManyMutation = (node: ObjectTypeDefinitionNode, inputNode: InputObjectTypeDefinitionNode | undefined): DocumentNode | undefined => {

    if (!(node.fields?.find(f => f.name.value === aggregateRootFieldName))) {
        return
    }

    const prefixedNodeName = node.name.value
    const nodeName = prefixedNodeName.substring(entityPrefix.length)

    if (inputNode) {
        return graphql`
        mutation deleteMany${nodeName}($input: [_DeleteMany${nodeName}Input!]!){
            packet{
                deleteMany${nodeName}(input:$input)
            }
        }`
    }
    else {
        return graphql`
        mutation deleteMany${nodeName}($ids: [ID!]!){
            packet{
                deleteMany${nodeName}(ids:$ids)
            }
        }`
    }
}





