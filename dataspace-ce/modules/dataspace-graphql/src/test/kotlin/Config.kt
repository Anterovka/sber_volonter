import com.fasterxml.jackson.databind.ObjectMapper
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.ttddyy.dsproxy.support.ProxyDataSource
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder

val dataSource: ProxyDataSource =
    ProxyDataSourceBuilder
        .create(
            HikariDataSource(
                HikariConfig().apply {
                    this.driverClassName = "org.postgresql.Driver"
                    this.jdbcUrl = System.getProperty("db.postgres.url")
                    this.username = System.getProperty("db.postgres.username")
                    this.password = System.getProperty("db.postgres.password")
                    isAutoCommit = false
                },
            ),
        ).logQueryBySlf4j()
        .build()

val objectMapper = ObjectMapper()
