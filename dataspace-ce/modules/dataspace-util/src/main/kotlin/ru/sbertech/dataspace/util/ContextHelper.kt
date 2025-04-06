package ru.sbertech.dataspace.util

import ru.sbertech.dataspace.entitymanager.EntityManager
import ru.sbertech.dataspace.entitymanager.EntityManagerFactory
import ru.sbertech.dataspace.model.Model
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson
import java.sql.Connection
import javax.sql.DataSource

class ContextHelper(
    val model: Model,
    private val entityManagerFactory: EntityManagerFactory,
    private val dataSource: DataSource,
    private val entitiesReadAccessJson: EntitiesReadAccessJson, // TODO Legacy){}
) {
    fun <R> withEntityManagerContext(block: Context.() -> R): R =
        dataSource.connection.use { connection ->
            try {
                with(
                    Context(
                        connection,
                        entityManagerFactory.create(connection),
                        model,
                        entitiesReadAccessJson,
                    ),
                ) {
                    this.block()
                }.also {
                    connection.commit()
                }
            } catch (e: Exception) {
                connection.rollback()
                throw e
            }
        }

    companion object {
        data class Context(
            val connection: Connection,
            val entityManager: EntityManager,
            val model: Model,
            val entitiesReadAccessJson: EntitiesReadAccessJson, // TODO Legacy){}
        )
    }
}
