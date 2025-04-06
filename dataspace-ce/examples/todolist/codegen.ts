import { CodegenConfig } from '@graphql-codegen/cli'

import { readFileSync, writeFileSync } from 'fs';

const replaceStr = "TOVvvVOT"
const replaceRegExp = new RegExp(replaceStr, "g")

const config: CodegenConfig = {
    overwrite: true,
    schema: './graphql/schema.graphql',
    documents: ['./graphql/**/*.graphql'],
    generates: {
        './src/__generate/requests.ts': {
            plugins: [
                'typescript',
                'typescript-operations',
                'typescript-react-apollo'
            ],

            documentTransforms: [
                {
                    transform: ({ documents }) => {
                        documents.forEach(d => {
                            d.document?.definitions.forEach(def => {

                                interface DocumentNode {
                                    arguments: Array<{
                                        kind: string,
                                        value: { kind: string, value: string }
                                    }>
                                    selectionSet: {
                                        selections: Array<DocumentNode>
                                    }
                                }

                                const updateNode = (documentNode: DocumentNode) => {
                                    const s = documentNode

                                    s.arguments?.forEach(a => {
                                        if (a.value?.value && a.kind == "Argument" && a.value.kind == "StringValue")
                                            a.value.value = a.value.value.replace(/\${/g, replaceStr + "${")
                                    })

                                    if (s.selectionSet && s.selectionSet.selections)
                                        s.selectionSet.selections.forEach(x => updateNode(x))
                                }

                                // @ts-ignore
                                const v: DocumentNode = def
                                updateNode(v)
                            })
                        })
                        return documents
                    }
                }
            ]
        }
    },
    hooks: {
        afterAllFileWrite: (args) => {
            writeFileSync(args, readFileSync(args, { encoding: "utf8" }).replace(replaceRegExp, '\\'))
        }
    }
}


export default config