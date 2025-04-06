package ru.sbertech.dataspace.util

object DsceConstants {
    const val MODEL_PATTERN = "{modelId:[0-9]+}"
    const val MODEL_CONTEXT_PATH = "/models/$MODEL_PATTERN"
    const val MODEL_GRAPHQL_PATH = "$MODEL_CONTEXT_PATH/graphql"
}
