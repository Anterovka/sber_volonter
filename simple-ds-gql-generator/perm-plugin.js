const { ReactApolloVisitor } = require('@graphql-codegen/typescript-react-apollo')

const { visit, concatAST, Kind } = require('graphql')

module.exports = {
  plugin: (schema, documents, config) => {
    const allAst = concatAST(documents.map(v => v.document));

    const allFragments = [
      ...(allAst.definitions.filter(d => d.kind === Kind.FRAGMENT_DEFINITION)).map(
        fragmentDef => ({
          node: fragmentDef,
          name: fragmentDef.name.value,
          onType: fragmentDef.typeCondition.name.value,
          isExternal: false,
        })
      ),
      ...(config.externalFragments || []),
    ];

    const visitor = new ReactApolloVisitor(schema, allFragments, config, documents);

    const visitorResult = visit(allAst, visitor);


    // Extract fragments from ReactApolloVisitor
    const visitorFragments =
      visitor.fragments
        .split("`")
        .map(x => x.trim())
        .filter(x => x.startsWith("fragment"))

    const fragmentDocJsonArray = visitorFragments.map(x => {
      const fragmentName = x.split(new RegExp(" "))[1].trim()
      return { "name": fragmentName + "FragmentDoc", "value": x }
    })


    // Extract mutations from ReactApolloVisitor
    const visitorMutations =
      JSON.stringify(visitorResult, null, 2)
        .split("`")
        .map(x => x.replace(/\\n/g, "\n").trim().replace(/\\/g, ""))
        .filter(x => { return (x.startsWith("mutation") || x.startsWith("query")) })

    const mutationDocJsonArray = visitorMutations.map(x => {
      const fragmentName = x.split(/[ ,(,{]/g)[1].trim()
      return { "name": fragmentName, "body": x, "allowEmptyChecks": true, "disableJwtVerification": true }
    })

    // Replace all frgaments placeholders in mutations
    mutationDocJsonArray.map(x => {
      let hasPlaceHolders = (x.body.indexOf("${") >= 0)
      let iterCount = 1
      while (hasPlaceHolders && iterCount < 100) {
        fragmentDocJsonArray.forEach(f => {
          const regExp = new RegExp('\\${' + f.name + '\\}', 'g')
          x.body = x.body.replace(regExp, f.value)
        })
        hasPlaceHolders = (x.body.indexOf("${") >= 0)
        iterCount++
      }
      return x
    })

    return JSON.stringify(mutationDocJsonArray, null, 2)
  }
}