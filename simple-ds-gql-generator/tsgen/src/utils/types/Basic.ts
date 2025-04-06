export interface FormGenConfig {
    generateChildCollectionField: boolean
}


export enum MutationKind {
    SIMPLE = 'SIMPLE',
    ON_INSTANCE = 'ON_INSTANCE'
}

export interface TypedField {
    fieldAlias: string
    fieldName: string
    fieldPath: string
    fieldType: string | TypedField[]
}

export interface TypedInput {
    inputName: string
    inputPath: string
    inputRefTypeName: {refAggName: string, refEntityName: string}
    inputType: string | TypedInput[]
}

export interface GeneratedGqlForAggregate {
    aggregateName: string
    aggregateEntityList: {
        entitiyName: string
        gqlStr: string
    }[]
}


export interface QueryData {
    name: string
    
}

export interface Query {
    queryAlias: string
    queryName: string
    queryType: string
    fieldList: TypedField[]
}

export interface Mutation {
    mutationAlias: string
    mutationName: string
    mutationType: MutationKind
    inputList: TypedInput[]
    getQueryFieldList: TypedField[]
}

export interface RefField {
    fieldName: string
    fieldType: string
    isCollection: boolean
    backRefereceAttributeName: string | undefined
}

export interface Entity {
    name: string
    queryList: Query[]
    mutationList: Mutation[]
    childEntityRefList: RefField[]

}

export interface GeneratedFcForAgg {
    aggName: string
    entityList: Entity[]
}
export interface AggList {
    aggList: GeneratedFcForAgg[]
}






