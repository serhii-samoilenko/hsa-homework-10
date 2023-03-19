package com.example

import com.example.util.ConnectionPool
import com.example.util.Database
import com.example.util.Report

data class SystemActor(
    private val name: String,
    private val connectionPool: ConnectionPool,
) {

    fun execute(vararg sql: String) {
        connectionPool.connection().use { connection ->
            val duration = Database.execute(connection, *sql)
            Report.sql(sql.toList(), name, duration.toString())
        }
    }

    fun tryExecute(sql: String) {
        connectionPool.connection().use { connection ->
            val duration = Database.tryExecute(connection, sql)
            Report.sql(listOf(sql), "Trying - $name", duration.toString())
        }
    }

    fun querySingleValue(sql: String): Any {
        connectionPool.connection().use { connection ->
            val value = Database.querySingleValue(connection, sql)
            Report.sql(listOf(sql), name, value.toString())
            return value
        }
    }
}
