package com.example

import com.example.util.ConnectionPool
import com.example.util.Database
import com.example.util.Report
import java.sql.Connection
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.time.Duration

data class UserActor(
    private val name: String,
    private val connectionPool: ConnectionPool,
    val report: Report,
) {
    private var connection: Connection? = null
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val futures = mutableListOf<Future<*>>()

    fun begin() {
        if (connection != null) {
            throw IllegalStateException("Already started connection")
        }
        connection = connectionPool.connection().apply { autoCommit = false }
        report.code("$name: began transaction")
    }

    fun execute(vararg sql: String) {
        var error: Exception? = null
        val statements = mutableListOf<String>()
        try {
            sql.forEach {
                statements.add(it)
                Database.execute(checkConnection(), it)
            }
        } catch (e: Exception) {
            error = e
        }
        report.sql(statements, name, error?.message)
    }

    fun schedule(operation: (UserActor) -> Unit): Waiter {
        futures.add(executor.submit { operation(this) })
        return Waiter(futures.toList())
    }

    fun tryExecute(sql: String) {
        val duration = Database.tryExecute(checkConnection(), sql)
        report.sql(listOf(sql), "Trying - $name", duration.toString())
    }

    fun querySingleValue(sql: String): Any {
        val value = Database.querySingleValue(checkConnection(), sql)
        report.sql(listOf(sql), name, value.toString())
        return value
    }

    fun queryRowValues(sql: String): List<Any> {
        val value = Database.queryRowValues(checkConnection(), sql).toList()
        report.sql(listOf(sql), name, value.toString())
        return value
    }

    fun commit() {
        with(checkConnection()) {
            commit()
            close()
            report.code("$name: committed")
        }
        connection = null
    }

    fun rollback() {
        with(checkConnection()) {
            rollback()
            close()
        }
        connection = null
    }

    fun awaitCompletion() {
        futures.forEach { it.get() }
        futures.clear()
    }

    private fun checkConnection(): Connection {
        if (connection == null) {
            throw IllegalStateException("Connection not started")
        }
        return connection!!
    }

    inner class Waiter(private val futures: List<Future<*>>) {
        fun tryAwait(timeout: String = "1s") {
            val duration = Duration.parse(timeout).inWholeMilliseconds
            // Wait for all futures to complete, return nothing on timeout
            val now = System.currentTimeMillis()
            val end = now + duration
            while (System.currentTimeMillis() < end) {
                if (futures.all { it.isDone || it.isCancelled }) {
                    return
                }
                MILLISECONDS.sleep(10)
            }
            report.code("$name: not finished after $timeout")
        }
    }
}
