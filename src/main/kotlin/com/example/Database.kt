package com.example

import java.sql.Connection
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object Database {
    fun execute(connection: Connection, vararg sql: String): Duration {
        val start = System.currentTimeMillis()
        sql.forEach {
            connection.createStatement().executeUpdate(it)
        }
        val end = System.currentTimeMillis()
        return (end - start).milliseconds
    }

    fun tryExecute(connection: Connection, sql: String) = try {
        execute(connection, sql)
    } catch (e: Exception) {
        println("Ignoring exception: ${e.message}")
        null
    }

    fun query(connection: Connection, sql: String): List<Map<String, Any>> {
        connection.createStatement().executeQuery(sql).use { rs ->
            val columns = (1..rs.metaData.columnCount).map { rs.metaData.getColumnName(it) }
            val rows = mutableListOf<Map<String, Any>>()
            while (rs.next()) {
                rows.add(columns.associateWith { rs.getObject(it) })
            }
            rs.close()
            return rows
        }
    }

    fun querySingleValue(connection: Connection, sql: String): Any = query(connection, sql).first().values.first()
}
