package ru.sbertech.dataspace.test.sql.dialect.support.context

import ru.sbertech.dataspace.primitive.Text
import ru.sbertech.dataspace.sql.dialect.Dialect
import java.util.UUID
import javax.sql.DataSource

abstract class QueryTestContext {
    abstract val dataSource: DataSource?

    abstract val dialect: Dialect

    abstract val expectedQueryString: String

    private val randomStringByName = hashMapOf<String, String>()

    private val randomTextByName = hashMapOf<String, Text>()

    fun randomString(name: String): String = randomStringByName.getOrPut(name) { UUID.randomUUID().toString() }

    fun randomText(name: String): Text =
        randomTextByName.getOrPut(name) {
            Text(StringBuilder().append(UUID.randomUUID()).apply { while (length < 8000) append(this) }.toString())
        }
}
