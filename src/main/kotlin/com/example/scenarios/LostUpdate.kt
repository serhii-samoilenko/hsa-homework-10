package com.example.scenarios

import com.example.SystemActor
import com.example.UserActor
import com.example.util.ConnectionPool
import com.example.util.Helper
import com.example.util.Report

fun lostUpdateScenario(helper: Helper, r: Report) = with(helper) {
    val databases = listOf("MySQL" to mysqlConnectionPool, "Postgres" to postgresConnectionPool)
    val isolationLevels = listOf("READ UNCOMMITTED", "READ COMMITTED", "REPEATABLE READ", "SERIALIZABLE")

    r.h2("Lost update")
    r.text(
        """
        We will use two transactions to update the same row in the database.
        The first transaction will read the value of the row, increment it by 1 and write it back.
        The second transaction will read the value of the row, increment it by 2 and write it back.
        
        We will run the two transactions in parallel.
        
        We will use different isolation levels to see how they affect the result.
        """.trimIndent(),
    )

    val scenario = fun(db: String, pool: ConnectionPool, level: String) {
        r.h3("$db with $level:")

        val alice = UserActor("Alice", pool, r)
        val bob = UserActor("Bob", pool, r)
        val system = SystemActor("System", pool, r)

        alice.begin()
        bob.begin()
        bob.execute("SET TRANSACTION ISOLATION LEVEL $level")

        r.line()
        r.text("Alice will select the value of the row and increment it")
        alice.schedule {
            val aliceValue = it.querySingleValue("SELECT age FROM persons WHERE name = 'Alice'") as Int
            it.execute("UPDATE persons SET age = ${aliceValue + 1} WHERE name = 'Alice'")
        }.tryAwait()

        r.text("Bob will select the value of the row and increment it")
        bob.schedule {
            val bobValue = it.querySingleValue("SELECT age FROM persons WHERE name = 'Alice'") as Int
            it.execute("UPDATE persons SET age = ${bobValue + 2} WHERE name = 'Alice'")
        }.tryAwait()
        r.line()

        r.text("Alice will commit")
        alice.schedule { it.commit() }.tryAwait()
        r.text("Bob will commit")
        bob.schedule { it.commit() }

        alice.awaitCompletion()
        bob.awaitCompletion()
        r.line()

        r.text("The value of the row is now:")
        val value = system.querySingleValue("SELECT age FROM persons WHERE name = 'Alice'") as Int
        r.text("Value: `$value`")
    }

    databases.forEach { (name, pool) ->
        isolationLevels.forEach { level ->
            scenario(name, pool, level)
        }
    }
}
