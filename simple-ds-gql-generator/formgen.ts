import { CodegenConfig } from '@graphql-codegen/cli'
import { existsSync, mkdirSync, readFileSync, writeFileSync } from 'fs'
import { AggList, FormGenConfig } from './tsgen/src/utils/types/Basic';

import { getMutationFormFC, getEntityBaseFC, getEntityUpdateFC, getEntityCreateFC, getEntityListFC, extractPrimitiveFieldNameList, getMainMenuFC, getAggMenuFC, getEntityDeleteFC } from './tsgen/src/formgen-templates/antd/FCTmpl'
import { rootDictionaryTypeName, updateOrCreatePrefix, createPrefix, updatePrefix, deletePrefix } from './tsgen/src/utils/Constants'

const config: CodegenConfig = {

  overwrite: true,
  schema: 'src/graphql/schema.graphql',
  documents: ['src/graphql/**/*.graphql'],
  generates: {
    'src/components/__generate/FormGenResult.json': {
      plugins: ['formgen-plugin.js'],
      config: { 
        generateChildCollectionField: true
      } as FormGenConfig
    }
  },
  hooks: {
    afterAllFileWrite: (args) => {
      const basePath = args.slice(0, args.lastIndexOf('/') + 1)
      const generatedFcForApp = JSON.parse(readFileSync(args, { encoding: "utf8" })) as AggList

      writeFileSync(basePath + "MainMenu.tsx", getMainMenuFC(generatedFcForApp.aggList.map(a => a.aggName)))

      generatedFcForApp.aggList.forEach(agg => {

        const aggName = agg.aggName
        const isDictionary = aggName === rootDictionaryTypeName
        const aggPath = basePath + aggName + "/"
        if (!existsSync(aggPath)) {
          mkdirSync(aggPath)
        }
        writeFileSync(aggPath + aggName + "Agg.tsx", getAggMenuFC(aggName, agg.entityList.map(e => e.name)))
        agg.entityList.forEach(entity => {

          const entityName = entity.name

          const entityPath = aggPath + entityName + "/"
          if (!existsSync(entityPath)) {
            mkdirSync(entityPath)
          }

          writeFileSync(entityPath + entityName + "Base.tsx", getEntityBaseFC(aggName, entityName))

          if (entity.queryList && entity.queryList.length > 0) {

            const getFirstQuery = entity.queryList[0]

            const entityListFC = getEntityListFC(aggName, entityName, extractPrimitiveFieldNameList(getFirstQuery.fieldList), entity.childEntityRefList)
            writeFileSync(entityPath + entityName + "List.tsx", entityListFC)

            if (entity.mutationList && entity.mutationList?.length > 0) {


              // Generate Create Form
              entity.mutationList.filter(m => m.mutationName === (isDictionary ? updateOrCreatePrefix : createPrefix) + entityName).forEach(m => {
                const entityMutationFormStr = getMutationFormFC(aggName, entityName, m, "Create", isDictionary)

                if (entityMutationFormStr) {
                  writeFileSync(entityPath + entityName + "CreateForm.tsx", entityMutationFormStr)
                  writeFileSync(entityPath + entityName + "Create.tsx", getEntityCreateFC(aggName, entityName, m))
                }
              })



              // Generate Update Form
              entity.mutationList.filter(m => m.mutationName === (isDictionary ? updateOrCreatePrefix : updatePrefix) + entityName).forEach(m => {
                const entityMutationFormStr = getMutationFormFC(aggName, entityName, m, "Update", isDictionary)

                if (entityMutationFormStr) {
                  writeFileSync(entityPath + entityName + "UpdateForm.tsx", entityMutationFormStr)
                  writeFileSync(entityPath + entityName + "Update.tsx", getEntityUpdateFC(aggName, entityName, m))
                }
              })

              // Generate Delete Form
              entity.mutationList.filter(m => m.mutationName === deletePrefix + entityName).forEach(m => {
                  writeFileSync(entityPath + entityName + "Delete.tsx", getEntityDeleteFC(aggName, entityName, m))
              })
            }
          }
        })
      })
    }
  }
}

export default config