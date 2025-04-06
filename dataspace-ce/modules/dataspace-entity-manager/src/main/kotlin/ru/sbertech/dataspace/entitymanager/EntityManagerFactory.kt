package ru.sbertech.dataspace.entitymanager

import java.sql.Connection

interface EntityManagerFactory {
    fun create(connection: Connection): EntityManager
}
