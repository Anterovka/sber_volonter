package ru.sbertech.dataspace

import org.springframework.boot.runApplication
import ru.sbertech.dataspace.configs.ParentCtxConfig

fun main(args: Array<String>) {
    runApplication<ParentCtxConfig>(*args)
}
