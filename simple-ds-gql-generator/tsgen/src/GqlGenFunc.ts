import { DocumentNode } from 'graphql'
import { extractBusinessObjectTypeNodeList, extractEmbeddedObjectTypeNodeList, extractEntityName, extractInputObjectTypeForObjectType, extractInputObjectTypeNodeList, extractNodeAggregateName, isDictionaryDefinition } from './utils/BaseUtils'
import { getGetMutation, getCreateManyMutation, getCreateMutation, getDeleteManyMutation, getDeleteMutation, getFragment, getSearchQuery, getUpdateMutation, getUpdateOrCreateMutation } from './utils/GqlGenUtils'
import { print } from 'graphql/language/printer'
import { deleteManyInputTypePrefix } from './utils/Constants'
import { GeneratedGqlForAggregate } from './utils/types/Basic'


export const getGqlData = (astNode: DocumentNode, config: { entityList: string[] }): GeneratedGqlForAggregate[] => {

    const result: GeneratedGqlForAggregate[] = []

    const inputNodeList = extractInputObjectTypeNodeList(astNode)
    const businessObjectTypeNodeList = extractBusinessObjectTypeNodeList(astNode)
    const embeddedObjectTypeNodeList = extractEmbeddedObjectTypeNodeList(astNode)

    businessObjectTypeNodeList//.filter(f => !isDictionaryDefinition(f))
        .forEach(node => {

            const isDictionary = isDictionaryDefinition(node)

            const fragment = getFragment(
                node,
                businessObjectTypeNodeList,
                embeddedObjectTypeNodeList,
                true
            )

            const inputNode = extractInputObjectTypeForObjectType(inputNodeList, node)

            const updateOrCreateMutationNode = inputNode ? getUpdateOrCreateMutation(inputNode, isDictionary) : undefined
            const createManyMutationNode = getCreateManyMutation(node)

            const deleteManyInputNode = extractInputObjectTypeForObjectType(inputNodeList, node, deleteManyInputTypePrefix)
            const deleteManyMutationNode = getDeleteManyMutation(node, deleteManyInputNode)

            const businessObjectTypeGqlStr =
                print(fragment)
                + "\n" + print(getSearchQuery(node))
                + "\n" + print(getGetMutation(node, isDictionary, "ForUpdate"))
                + (isDictionary ? "" : ("\n" + print(getCreateMutation(node))))
                + (isDictionary ? "" : ("\n" + print(getUpdateMutation(node))))
                + (isDictionary ? "" : ("\n" + print(getDeleteMutation(node))))
                + (isDictionary ? ("\n" + print(updateOrCreateMutationNode!)) : "")
//                + ((updateOrCreateMutationNode) ? ("\n" + print(updateOrCreateMutationNode!)) : "")
//                + ((createManyMutationNode && !isDictionary) ? ("\n" + print(createManyMutationNode!)) : "")
//                + ((deleteManyMutationNode && !isDictionary) ? ("\n" + print(deleteManyMutationNode!)) : "")


            const nodeAggregateName = extractNodeAggregateName(node)
            const resultAggregate = result.find(f => f.aggregateName === nodeAggregateName) ??
                result[result.push({ aggregateName: extractEntityName(nodeAggregateName), aggregateEntityList: [] }) - 1]

            resultAggregate.aggregateEntityList.push({ entitiyName: extractEntityName(node.name.value), gqlStr: businessObjectTypeGqlStr })
        })


    return result
}


