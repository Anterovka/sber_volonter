import { CodegenConfig } from '@graphql-codegen/cli';

const config: CodegenConfig = {
    overwrite: true,
    schema: ['./graphql/schema.graphql'], // 'http://localhost:8083/graphql'
    documents: './graphql/**/*.graphql',
    generates: {
        './dumme-permissions.json':
            {
                plugins: ['./perm-plugin.js'],
            },
    },
};

export default config;
