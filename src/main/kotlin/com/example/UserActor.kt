package com.example

import java.sql.Connection
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

data class UserActor(
    private val name: String,
    private val connectionPool: ConnectionPool,
) {
    private var connection: Connection? = null
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val futures = mutableListOf<Future<*>>()

    fun begin() {
        if (connection != null) {
            throw IllegalStateException("Already started connection")
        }
        connection = connectionPool.connection().apply { autoCommit = false }
    }

    fun execute(vararg sql: String) {
        val duration = Database.execute(checkConnection(), *sql)
        Report.sql(sql.toList(), name)
    }

    fun executeAsync(vararg sql: String) {
        futures.add(executor.submit { execute(*sql) })
    }

    fun tryExecute(sql: String) {
        val duration = Database.tryExecute(checkConnection(), sql)
        Report.sql(listOf(sql), "Trying - $name", duration.toString())
    }

    fun querySingleValue(sql: String): Any {
        val value = Database.querySingleValue(checkConnection(), sql)
        Report.sql(listOf(sql), name, value.toString())
        return value
    }

    fun commit() {
        with(checkConnection()) {
            commit()
            close()
        }
        connection = null
    }

    fun commitAsync(also: () -> Unit) {
        val future = executor.submit {
            commit()
            also()
        }
        futures.add(future)
    }

    fun rollback() {
        with(checkConnection()) {
            rollback()
            close()
        }
        connection = null
    }

    fun await() {
        futures.forEach { it.get() }
        futures.clear()
    }

    private fun checkConnection(): Connection {
        if (connection == null) {
            throw IllegalStateException("Connection not started")
        }
        return connection!!
    }
}
