import { CodegenConfig } from '@graphql-codegen/cli'
import { readFileSync, writeFileSync, existsSync, mkdirSync } from 'fs';
import { GeneratedGqlForAggregate } from './tsgen/build/utils/types/Types';


const config: CodegenConfig = {
  overwrite: true,
  schema: 'src/graphql/schema.graphql',
  generates: {
    'src/graphql/__generate/gqlGenResult.json': {
      plugins: ['gqlgen-plugin.js'],
      config:{
         entityList:["Clinic"]
      }
      },
  },
  hooks: {
    afterAllFileWrite: (args) => {
      const basePath = args.slice(0, args.lastIndexOf('/') + 1)

      const generateResult = JSON.parse(readFileSync(args, { encoding: "utf8" })) as GeneratedGqlForAggregate[]


      generateResult.forEach(aggregate => {
        const path = basePath + aggregate.aggregateName + "/"
        if (!existsSync(path)) {
          mkdirSync(path)
        }
        aggregate.aggregateEntityList.forEach(entity => {
          writeFileSync(path + entity.entitiyName + ".graphql", entity.gqlStr)
        })
      })
    }
  }
}

export default config