const { getFormData } = require('./tsgen/build/FormGenFunc')
const { getCachedDocumentNodeFromSchema } = require('@graphql-codegen/plugin-helpers')
 
module.exports = {
  plugin(schema, documents, config) {
    const astNode = getCachedDocumentNodeFromSchema(schema)

    //return JSON.stringify(astNode,null, 2)


    return getFormData(astNode,documents, config)

  } 
}