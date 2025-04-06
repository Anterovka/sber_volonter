import { toLowerCaseFirstLetter, toUpperCaseFirstLetter } from "../../utils/BaseUtils"
import { updateOrCreatePrefix, updatePrefix, mandatoryFieldList, primitiveTypeList, referencePrefix, enumPrefix, referencePostfix, rootDictionaryTypeName, createPrefix } from "../../utils/Constants"
import { Mutation, MutationKind, Query, RefField, TypedField, TypedInput } from "../../utils/types/Basic"
import { camelCase } from 'change-case-all'

const { SIMPLE, ON_INSTANCE } = MutationKind



export const extractPrimitiveFieldNameList = (typedFieldList: TypedField[]): string[] => {
    return typedFieldList.filter(f => {
        return (
            !mandatoryFieldList.includes(f.fieldName) && f.fieldType && (
                primitiveTypeList.includes(f.fieldType.toString())
                || (typeof f.fieldType === 'string' && (
                    f.fieldType.startsWith(enumPrefix)
                    || (f.fieldType.startsWith(referencePrefix) && f.fieldType.endsWith(referencePostfix))
                ))

            )
        )
    }).map(f => f.fieldName)
}

export const getEntityListFC = (aggName: string, entityName: string, fieldNameList: string[], childEntityRefList: RefField[]): string => {

    const camelCasedEntityName = toUpperCaseFirstLetter(camelCase(entityName))

    const columnsStr = JSON.stringify(
        ["id", ...fieldNameList.concat(childEntityRefList.map(rf => rf.fieldName)),].map(f => { return { title: f, key: f, dataIndex: f } })
        , null, 2)

    const fieldMappingStr = fieldNameList.map(f => {
        return `                    ${f}: elem.${f},`
    }).join("\n")

    const childEntityMappingStr = childEntityRefList.map(f => {
        return `                    ${f.fieldName}: <Link to={\`/${aggName}Agg/${f.fieldType}/List/${f.backRefereceAttributeName}=\${elem.id}\`}>{"{***}"}</Link>,`
    }).join("\n")


    return "" +
        `import React, { FC, useState } from 'react';
import { Button, Space, Spin, Table } from 'antd';
import { Link, useNavigate, useParams } from 'react-router';
import { useApolloClient } from '@apollo/client';

import { extractParamName, extractParamValue } from '../../../../basic/Utils';
import ErrorModal from '../../../basic/ErrorModal';
import { useSearch${camelCasedEntityName}Query } from '../../../../__generate/graphql-frontend'

export const ${entityName}List: FC<{ selected${entityName}?: string | null, setSelected${entityName}?: (value: string) => void }> = ({ selected${entityName}, setSelected${entityName} }) => {

    const client = useApolloClient()

    const navigate = useNavigate();

    const { filterStr } = useParams()

    const columns = ${columnsStr}.concat((selected${entityName} === null) ? [] : [
        {
            "title": "actions",
            "key": "actions",
            "dataIndex": "actions"
        }])

    const [error, setError] = useState<Error | null>(null)

    const { data, loading, error: queryError } = useSearch${camelCasedEntityName}Query(
        {
            variables: {
                cond: (filterStr && filterStr !== 'undefined' && selected${entityName} !== null ) ? \`it.\${extractParamName(filterStr)}.id=='\${extractParamValue(filterStr)}'\` : null
            }
        })

    const elemList = data?.search${entityName}.elems

    const mapToView = (list: typeof elemList) => {
        return (
            list?.map(elem => {
                return {
                    key: elem.id ?? "",
          actions: <Space>
            <Link to={\`/${aggName}Agg/${entityName}/Update/\${elem.id}\`}>Update</Link>
            <Link to={\`/${aggName}Agg/${entityName}/Delete/\${elem.id}\`}>Delete</Link>
          </Space>,
          id: elem.id,
${fieldMappingStr}
${childEntityMappingStr}
                }
            })
        )
    }

    if (loading) return (<Spin tip="Loading..." />);
    if (queryError) {
        return (<ErrorModal error={queryError} setError={setError} />)
    } else {
    
        client.refetchQueries({include:["search${entityName}"]})
        return (
            <Space direction='vertical'>
                {selected${entityName} === undefined && <Button
                    onClick={() => {
                        navigate(\`/${aggName}Agg/${entityName}/Create/\${filterStr}\`)
                    }}
                >
                    Create
                </Button>}
        
                <Table
                    columns={columns}
                    dataSource={mapToView(elemList)}
                    onRow={(record) => ({ onClick: event => { if (setSelected${entityName}) setSelected${entityName}(record.id) } })}
                />
            </Space>
        )
    }
}
`
}


export const getMainMenuFC = (aggNameList: string[]): string => {

    const tabContentImportListStr = aggNameList.map(aggName => {
        return `import { ${aggName}Agg, ${aggName}AggRouteList } from './${aggName}/${aggName}Agg'`
    }).join("\n")

    const menuItemListrStr = aggNameList.map(aggName => {
        return `
                        <Menu.Item key='${aggName}Agg'>
                            <Link to={'/${aggName}Agg'}>${aggName}Agg</Link>
                        </Menu.Item>`
    }).join("")

    const routeListrStr = aggNameList.map(aggName => {
        return `
                        <Route key={"${aggName}Agg"} path="${aggName}Agg" element={<${aggName}Agg />}>
                            {${aggName}AggRouteList()}
                        </Route>`
    }).join("")


    return "" +
        `import React, { FC } from 'react';
import { Layout, Menu } from 'antd';
import { BrowserRouter, Link, Route, Routes } from 'react-router';
import { Home } from '../Home';

${tabContentImportListStr}

const { Header } = Layout;


export const MainMenu: FC = () => {
    return (
        <BrowserRouter>
            <Layout>
                <Header>
                    <Menu mode='horizontal' >
${menuItemListrStr}
                    </Menu>
                </Header>
                <Routes>
                    <Route key={"Home"} path="/" element={<Home />}>
${routeListrStr}
                    </Route>
                </Routes>
            </Layout>
        </BrowserRouter>
    )
}`
}

export const getAggMenuFC = (aggName: string, entityNameList: string[]): string => {

    const isDictionary: boolean = aggName === rootDictionaryTypeName

    const contentImportListStr = entityNameList.map(entityName => {
        return `import { ${entityName}List } from './${entityName}/${entityName}List'
import { ${entityName}Base } from './${entityName}/${entityName}Base';
import { ${entityName}Create } from './${entityName}/${entityName}Create';
import { ${entityName}Update } from './${entityName}/${entityName}Update';` +
            (isDictionary ? "" : `import { ${entityName}Delete } from './${entityName}/${entityName}Delete';`)
    }).join("\n")

    const menuItemListrStr = entityNameList.map(entityName => {
        return `
                    <Menu.Item key='${entityName}'>
                        <Link to={'/${aggName}Agg/${entityName}/List'}>${entityName}</Link>
                    </Menu.Item>`
    }).join("")

    const routeListrStr = entityNameList.map(entityName => {
        return `
        <Route key={"${entityName}"} path="${entityName}" element={<${entityName}Base />}>
            <Route key={"List"} path="List" element={<${entityName}List />} />
            <Route key={"ListWithFilter"} path="List/:filterStr" element={<${entityName}List />} />
            <Route key={"Create"} path="Create" element={<${entityName}Create />} />
            <Route key={"CreateWithFilter"} path="Create/:filterStr" element={<${entityName}Create />} />
            <Route key={"Update"} path="Update/:${toLowerCaseFirstLetter(entityName)}Id" element={<${entityName}Update />} />` +
            (isDictionary ? "" : `            <Route key={"Delete"} path="Delete/:${toLowerCaseFirstLetter(entityName)}Id" element={<${entityName}Delete />} />
`) +
            `        </Route>`
    }).join("")


    return "" +
        `import React, { FC } from 'react';
import { Layout, Menu } from 'antd';
import { Link, Outlet, Route } from 'react-router';

${contentImportListStr}

const { Sider } = Layout;

export const ${aggName}Agg: FC = () => {
    return (
        <Layout>
            <Sider>
                <Menu mode='vertical' >
${menuItemListrStr}
                </Menu>
            </Sider>
            <Outlet />
        </Layout>
    )
}

export const ${aggName}AggRouteList = () => {
    return <>
${routeListrStr}
    </>;
}
`
}


export const getMutationFormFC = (aggName: string, entityName: string, mutation: Mutation, formPostfix: string, isDictionary: boolean): string => {

    //if (mutation.mutationType === ON_INSTANCE && query) return getMapToInputMutationFC(mutation, query)

    if (mutation.mutationName.startsWith(createPrefix))
        return getMutationFieldFormFC(mutation, entityName, formPostfix, isDictionary)

    if (mutation.mutationName.startsWith(updatePrefix))
        return getMutationFieldFormFC(mutation, entityName, formPostfix, isDictionary)

    if (mutation.mutationName.startsWith(updateOrCreatePrefix))
        return getMutationFieldFormFC(mutation, entityName, formPostfix, isDictionary)




    return ""
}
const getMapToInputMutationFC = (mutation: Mutation, query: Query): string => {

    const mapToInputFunction = getMapToInutFunction(mutation, query)

    return ""
}

const getMapToInutFunction = (mutation: Mutation, query: Query): string => {

    const fieldMappingList: string[] = mutation.inputList.map(f => {

        return ""
    })


    return `
function mapToInput(data: PersonAttributesFragment): InputParameters {
    return {
        birthDate: data.birthDate,
        firstName: data.firstName,
        id: data.id,
        lastName: data.lastName,
        patronymic: data.patronymic,
    }
}
`

}


const getMutationFieldFormFC = (mutation: Mutation, entityName: string, formPostfix: string, isDictionary: boolean): string => {


    const input = mutation.inputList.find(il => il.inputName = "input")

    if (input && typeof input.inputType != 'string') {

        const fieldToFormList = input.inputType.map(it => extractFormItemListStr(it))

        const importListStr = fieldToFormList.map(f => f?.importStr).join("")
        const formItemListStr = fieldToFormList.map(f => f?.formItemStr).join("")
        const stateParamListStr = fieldToFormList.map(f => f?.stateParamStr).join("")
        const modalListStr = fieldToFormList.map(f => f?.modalStr).join("")



        // const formItemListStr = input.inputType.map(it => {
        //     return extractFormItemListStr(it)?.formItemList
        // }).join("")

        // const importStr = input.inputType.map(it => {
        //     return extractFormItemListStr(it)?.importStr
        // }).join("")


        return `
import React, { FC, useState } from 'react';

import { DatePicker, Checkbox, Form, Input, Select, Space, Button, Modal  } from 'antd';
const { Option } = Select

import { InputParameters } from './${entityName}${formPostfix}';
import moment from 'moment';
${importListStr}

interface FormProps{
    inputParameters: InputParameters
    changeInputParameters: (input: InputParameters) => void
}

export const ${entityName}${formPostfix}Form: FC<FormProps> = ({ inputParameters, changeInputParameters }) => {

${stateParamListStr}

    return (
        <>
            <Form>
${formItemListStr}
            </Form>
${modalListStr}
        </>
    )
}`
    }

    return ""
}

const extractFormItemListStr = (typedInput: TypedInput): { importStr: string, formItemStr: string, stateParamStr: string, modalStr } | undefined => {
    const typedInputName = typedInput.inputName

    if (typedInput.inputRefTypeName.refAggName === "" ) {

        if (typedInput.inputType === 'String' || typedInput.inputType === 'ID')
            return {
                formItemStr: `
            <Form.Item label="${typedInputName}">
                <Input
                    value={inputParameters.${typedInputName}!}
                    onChange={e => changeInputParameters({ ${typedInputName}: e.target.value })}
                />
            </Form.Item>`, importStr: "", stateParamStr: "", modalStr: ""
            }


        if (typedInput.inputType === '_Date')
            return {
                formItemStr: `
            <Form.Item label="${typedInputName}">
                <DatePicker
                    value={inputParameters.${typedInputName} ? moment(inputParameters.${typedInputName}, "YYYY-MM-DD") : null}
                    onChange={moment => changeInputParameters({ ${typedInputName}: moment?.format("YYYY-MM-DD") })}
                    format="YYYY-MM-DD"
                />
            </Form.Item>`, importStr: "", stateParamStr: "", modalStr: ""
            }


            if (typedInput.inputType === '_DateTime')
                return {
                    formItemStr: `
            <Form.Item label="${typedInputName}">
                <DatePicker
                    showTime={{ defaultValue: moment('00:00:00', 'HH:mm:ss') }}
                    value={inputParameters.${typedInputName} ? moment(inputParameters.${typedInputName}, "YYYY-MM-DD HH:mm:ss") : null}
                    onChange={moment => changeInputParameters({ ${typedInputName}: moment?.format("YYYY-MM-DDTHH:mm:ss") })}
                    format="YYYY-MM-DD HH:mm:ss"
                />
            </Form.Item>`, importStr: "", stateParamStr: "", modalStr: ""
                }
    


        if (typedInput.inputType === 'Boolean')
            return {
                formItemStr: `
            <Form.Item label="${typedInputName}">
                <Checkbox
                    checked={inputParameters?.${typedInputName}!}
                    onChange={e => {
                        changeInputParameters({ ${typedInputName}: e.target.checked })
                    }
                    }
                />
            </Form.Item>`, importStr: "", stateParamStr: "", modalStr: ""
            }

        if (typeof typedInput.inputType === 'string' && typedInput.inputType.startsWith(enumPrefix)) {
            const enumTypeName = typedInput.inputType.substring(enumPrefix.length)

            return {
                formItemStr: `
            <Form.Item>
                <Select showArrow={true}
                    onChange={(value) => {
                        changeInputParameters({ ${typedInputName}: value })
                    }}
                    value={inputParameters?.${typedInputName}!}
                >
                    {Object.keys(_En_${enumTypeName}).map(it =>
                        <Option key={it} value={_En_${enumTypeName}[it as keyof typeof _En_${enumTypeName}]}>
                            {it}
                        </Option>
                    )}
                </Select>
            </Form.Item>
`, importStr: `import {_En_${enumTypeName} } from '../../../../__generate/graphql-frontend'
`, stateParamStr: "", modalStr: ""
            }
        }
    } else {
        const refEntityName = typedInput.inputRefTypeName.refEntityName
        const refAggName = typedInput.inputRefTypeName.refAggName
        const isExtRef = refEntityName.startsWith(referencePrefix) && refEntityName.endsWith(referencePostfix)
        const refTypeName = isExtRef ?
            refEntityName.substring(referencePrefix.length).slice(0, -referencePostfix.length) :
            refEntityName

        const upperCaseTypedInputName = toUpperCaseFirstLetter(typedInputName)

        return {
            formItemStr: `
            <Form.Item label="${typedInputName}">
                <Space>
                    <Input
                        value={inputParameters.${isExtRef ? typedInputName + "?.entityId!" : typedInputName}}
                        onChange={e => changeInputParameters({ ${typedInputName}: ${isExtRef ? "{ entityId: e.target.value }" : "e.target.value"}})}
                    />
                    <Button onClick={() => setSelected${upperCaseTypedInputName}Id(null)}>{"..."}</Button>
                </Space>
            </Form.Item>`
            , importStr: `import { ${refTypeName}List } from '../../${refAggName}/${refTypeName}/${refTypeName}List';`
            , stateParamStr: `
    const [selected${upperCaseTypedInputName}Id, setSelected${upperCaseTypedInputName}Id] = useState<string | undefined | null>()
    if (selected${upperCaseTypedInputName}Id) {
        changeInputParameters({ ${typedInputName}: ${isExtRef ? "{ entityId: selected" + upperCaseTypedInputName + "Id }" : "selected" + upperCaseTypedInputName + "Id"} })
        setSelected${upperCaseTypedInputName}Id(undefined)
    }`
            , modalStr: `
    <Modal
        visible = { selected${upperCaseTypedInputName}Id === null}
        footer = { null}
        width = "90%"
        onCancel = {() => setSelected${upperCaseTypedInputName}Id(undefined)}
    >
        <${refTypeName}List selected${refTypeName}={selected${upperCaseTypedInputName}Id } setSelected${refTypeName}={setSelected${upperCaseTypedInputName}Id }> </${refTypeName}List>
    </Modal>`
        }

    }
    return undefined
}


export const getEntityBaseFC = (aggName: string, entityName: string): string => {
    return `
import React, { FC } from 'react';
import { Outlet } from 'react-router';


export const ${entityName}Base: FC = () => {
    return (
        <>
            <Outlet />
        </>
    )
}`
}

export const getEntityUpdateFC = (aggName: string, entityName: string, mutation: Mutation): string => {

    const camelCasedEntityName = toUpperCaseFirstLetter(camelCase(entityName))

    const mutationName = mutation.mutationName
    const upperFirstLetterMutationName = toUpperCaseFirstLetter(camelCase(mutation.mutationName))

    const updateInputPrefix = aggName === rootDictionaryTypeName ? "Create" : "Update"


    const input = mutation.inputList.find(il => il.inputName = "input")

    if (input && typeof input.inputType != 'string') {

        const mapToInputListStr = input.inputType.map(it => {
            return extractMapToInputStr(it)
        }).join("")

        return `
import React, { FC, useEffect, useState } from 'react';

import { Button, Space, Spin } from 'antd';

import ErrorModal from '../../../basic/ErrorModal';

import { ${camelCasedEntityName}AttributesFragment, _${updateInputPrefix}${camelCasedEntityName}Input, useGetForUpdate${camelCasedEntityName}Mutation, use${upperFirstLetterMutationName}Mutation } from '../../../../__generate/graphql-frontend'
import { ${entityName}UpdateForm } from './${entityName}UpdateForm';

import { useParams, useNavigate, Outlet } from 'react-router'


export type InputParameters = Partial<_${updateInputPrefix}${camelCasedEntityName}Input>

function mapToInput(data: ${camelCasedEntityName}AttributesFragment): InputParameters {
    return {
${mapToInputListStr}
    }
}

export const ${entityName}Update: FC = () => {

    const navigate = useNavigate();

    const { ${toLowerCaseFirstLetter(entityName)}Id } = useParams()

    const [get${camelCasedEntityName}Mutation, { data: dataGet, loading: loadingGet, error: errorGet }] = useGetForUpdate${camelCasedEntityName}Mutation()
    useEffect(() => {
        if (${toLowerCaseFirstLetter(entityName)}Id)
            get${camelCasedEntityName}Mutation({ variables: { id: ${toLowerCaseFirstLetter(entityName)}Id } })
    }, [])

    const ${toLowerCaseFirstLetter(entityName)}Data = dataGet?.packet?.get${entityName}
    useEffect(() => {
        if (${toLowerCaseFirstLetter(entityName)}Data)
            setInputParameters(mapToInput(${toLowerCaseFirstLetter(entityName)}Data))
    }, [${toLowerCaseFirstLetter(entityName)}Data])

    const [${mutationName}Mutation, { error: errorUpdate, data: dataUpdate, loading: loadingUpdate }] = use${upperFirstLetterMutationName}Mutation()

    const [inputParameters, setInputParameters] = useState<InputParameters>({})
    const changeInputParameters = (params: InputParameters) => {
        var input = { ...inputParameters }
        setInputParameters(Object.assign(input, params))
    }

    const [error, setError] = useState<Error | null>(null)
    useEffect(() => {
        const err = [errorGet, errorUpdate].find(e => e)
        if (err) {
            setError(err)
        }

    }, [errorGet, errorUpdate])


    if (loadingGet || loadingUpdate) return (<Spin tip="Loading..." />);
    if (error) return (<ErrorModal error={error} setError={setError} />)

    return (
        <>
            <Space direction='vertical'>
                <${entityName}UpdateForm inputParameters={inputParameters} changeInputParameters={changeInputParameters}></${entityName}UpdateForm>
                <Space>
                    <Button type="primary" htmlType="submit"
                        onClick={() => {
                            ${mutationName}Mutation({ variables: { input: Object.assign(inputParameters) as _${updateInputPrefix}${camelCasedEntityName}Input } }).then(() => {
                                navigate("/${aggName}Agg/${entityName}/List")
                            })
                        }}
                    >
                        OK
                    </Button>
                    <Button
                        onClick={() => {
                            navigate("/${aggName}Agg/${entityName}/List")
                        }}
                    >
                        Cancel
                    </Button>
                </Space>
            </Space>
        </>
    )
}`
    }
    return ""
}



const extractMapToInputStr = (typedInput: TypedInput): string => {
    const typedInputName = typedInput.inputName

    if (typedInput.inputType === 'ID' && typedInputName !== "id")
        return `
        ${typedInputName}: data.${typedInputName}?.id,`

    if (typedInput.inputType === 'String' || typedInput.inputType === 'ID' || typedInput.inputType === '_Date' || (typeof typedInput.inputType === "string" && typedInput.inputType.startsWith(enumPrefix)))
        return `
        ${typedInputName}: data.${typedInputName},`

    const refEntityName = typedInput.inputRefTypeName.refEntityName
    if (refEntityName.startsWith(referencePrefix) && refEntityName.endsWith(referencePostfix)) {
        return `
        ${typedInputName}: {entityId: data.${typedInputName}.entityId!},`
    }

    return ""
}

export const getEntityCreateFC = (aggName: string, entityName: string, mutation: Mutation): string => {

    const camelCasedEntityName = toUpperCaseFirstLetter(camelCase(entityName))

    const mutationName = mutation.mutationName
    const upperFirstLetterMutationName = toUpperCaseFirstLetter(camelCase(mutation.mutationName))


    const input = mutation.inputList.find(il => il.inputName = "input")

    if (input && typeof input.inputType != 'string') {

        return `
import React, { FC, useEffect, useState } from 'react';

import { Button, Space, Spin } from 'antd';

import ErrorModal from '../../../basic/ErrorModal';

import { _Create${camelCasedEntityName}Input, use${upperFirstLetterMutationName}Mutation } from '../../../../__generate/graphql-frontend'
import { ${camelCasedEntityName}CreateForm } from './${camelCasedEntityName}CreateForm';

import { useNavigate, useParams } from 'react-router'
import { extractParamName, extractParamValue } from '../../../../basic/Utils';

export type InputParameters = Partial<_Create${camelCasedEntityName}Input>

export const ${camelCasedEntityName}Create: FC = () => {

    const navigate = useNavigate();

    const { filterStr } = useParams()

    const [${mutationName}Mutation, { error: errorCreate, data: dataCreate, loading: loadingCreate }] = use${upperFirstLetterMutationName}Mutation()

    const [inputParameters, setInputParameters] = useState<InputParameters>({})
    const changeInputParameters = (params: InputParameters) => {
        var input = { ...inputParameters }
        setInputParameters(Object.assign(input, params))
    }

    const [error, setError] = useState<Error | null>(null)
    useEffect(() => {
        const err = [errorCreate].find(e => e)
        if (err) {
            setError(err)
        }

    }, [errorCreate])


    if (loadingCreate) return (<Spin>Loading...</Spin>)
    if (error) return (<ErrorModal error={error} setError={setError} />)

    return (
        <>
            <Space direction='vertical'>
                <${camelCasedEntityName}CreateForm 
                    inputParameters={filterStr ? Object.assign(inputParameters, { [extractParamName(filterStr)]: extractParamValue(filterStr) }) : inputParameters}
                    changeInputParameters={changeInputParameters}
                ></${camelCasedEntityName}CreateForm>
                <Space>
                    <Button type="primary" htmlType="submit"
                        onClick={() => {
                            ${mutationName}Mutation({ variables: { input: Object.assign(inputParameters) as _Create${camelCasedEntityName}Input } }).then(() => {
                                navigate("/${aggName}Agg/${camelCasedEntityName}/List/" + (filterStr === 'undefined' ? "" : filterStr))
                            })
                        }}
                    >
                        OK
                    </Button>
                    <Button
                        onClick={() => {
                            navigate("/${aggName}Agg/${camelCasedEntityName}/List/" + (filterStr === 'undefined' ? "" : filterStr))
                        }}
                    >
                        Cancel
                    </Button>
                </Space>
            </Space>
        </>
    )
}

`
    }
    return ""
}

export const getEntityDeleteFC = (aggName: string, entityName: string, mutation: Mutation): string => {

    const camelCasedEntityName = toUpperCaseFirstLetter(camelCase(entityName))

    const mutationName = mutation.mutationName
    const upperFirstLetterMutationName = toUpperCaseFirstLetter(camelCase(mutation.mutationName))

    return `
import React, { FC, useEffect, useState } from 'react';

import { Button, Space, Spin } from 'antd';

import ErrorModal from '../../../basic/ErrorModal';
import { use${upperFirstLetterMutationName}Mutation } from '../../../../__generate/graphql-frontend'
import { useNavigate, useParams } from 'react-router'

export const ${camelCasedEntityName}Delete: FC = () => {

    const navigate = useNavigate();

    const { ${toLowerCaseFirstLetter(entityName)}Id } = useParams()

    const [${mutationName}Mutation, { error: errorDelete, data: dataDelete, loading: loadingDelete }] = use${upperFirstLetterMutationName}Mutation()

    const [error, setError] = useState<Error | null>(null)
    useEffect(() => {
        const err = [errorDelete].find(e => e)
        if (err) {
            setError(err)
        }

    }, [errorDelete])

    if (loadingDelete) return (<Spin>Loading...</Spin>)
    if (errorDelete) return (<ErrorModal error={errorDelete} setError={setError} />)

    return (
        <>
            <Space direction='vertical'>
                <>{\`Delete ${entityName} with ID:\${${toLowerCaseFirstLetter(entityName)}Id}?\`}</>
                <Space>
                    <Button type="primary" htmlType="submit"
                        onClick={() => {
                            if (${toLowerCaseFirstLetter(entityName)}Id)
                                ${mutationName}Mutation({ variables: { id: ${toLowerCaseFirstLetter(entityName)}Id } }).then(() => {
                                    navigate("/${aggName}Agg/${entityName}/List")
                                })
                        }}
                    >
                        OK
                    </Button>
                    <Button
                        onClick={() => {
                            navigate("/${aggName}Agg/${entityName}/List")
                        }}
                    >
                        Cancel
                    </Button>
                </Space>
            </Space>
        </>
    )
}`
}

