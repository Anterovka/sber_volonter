export const extractParamName = (paramStr: string) => {
    return paramStr.split("=")[0]
}

export const extractParamValue = (paramStr: string) => {
    return paramStr.split("=")[1]
}