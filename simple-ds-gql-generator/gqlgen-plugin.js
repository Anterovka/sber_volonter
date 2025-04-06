const { getGqlData } = require('./tsgen/build/GqlGenFunc')
const { getCachedDocumentNodeFromSchema } = require('@graphql-codegen/plugin-helpers')

module.exports = {
  plugin(schema, documents, config) {
    const astNode = getCachedDocumentNodeFromSchema(schema)
    //return JSON.stringify(astNode, null, 2)
    return JSON.stringify(getGqlData(astNode, config), null, 2)
  }
}