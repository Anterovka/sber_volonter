package ru.sbertech.dataspace.test.sql.dialect.support

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions.assertThat
import ru.sbertech.dataspace.primitive.Primitive
import ru.sbertech.dataspace.primitive.accept
import javax.sql.DataSource

fun dataSource(
    driverClassName: String,
    jdbcUrl: String?,
    username: String?,
    password: String?,
): DataSource? =
    if (jdbcUrl == null) {
        null
    } else {
        HikariDataSource(
            HikariConfig().apply {
                this.driverClassName = driverClassName
                this.jdbcUrl = jdbcUrl
                this.username = username
                this.password = password
                isAutoCommit = false
            },
        )
    }

fun assertThatPrimitive(primitive: Primitive?): AbstractAssert<*, *> = primitive?.accept(AssertReturningVisitor) ?: assertThat(primitive)
