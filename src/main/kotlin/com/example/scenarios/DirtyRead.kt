package com.example.scenarios

import com.example.UserActor
import com.example.util.ConnectionPool
import com.example.util.Helper
import com.example.util.Report.h2
import com.example.util.Report.h3
import com.example.util.Report.text

fun dirtyReadScenario(helper: Helper) = with(helper) {
    val databases = listOf("MySQL" to mysqlConnectionPool, "Postgres" to postgresConnectionPool)
    val isolationLevels = listOf("READ UNCOMMITTED", "READ COMMITTED", "REPEATABLE READ", "SERIALIZABLE")

    h2("Dirty read")
    text(
        """
        The first transaction will perform two reads of the same row.
        The second transaction will update the row between the two reads.
        
        We will use different isolation levels to see how they affect the result.
        """.trimIndent(),
    )

    val scenario = fun(db: String, pool: ConnectionPool, level: String) {
        h3("$db with $level:")

        val alice = UserActor("Alice", pool)
        val bob = UserActor("Bob", pool)

        alice.begin()
        alice.execute("SET TRANSACTION ISOLATION LEVEL $level")

        text("Alice will select the value of the row")
        val aliceValue = alice.querySingleValue("SELECT age FROM persons WHERE name = 'Bob'") as Int

        text("Bob will select the value of the row and increment it")
        bob.begin()
        bob.schedule {
            it.execute("UPDATE persons SET age = ${aliceValue + 2} WHERE name = 'Bob'")
        }.tryAwait()

        text("Alice will AGAIN select the value of the row")
        val newAliceValue = alice.querySingleValue("SELECT age FROM persons WHERE name = 'Bob'") as Int

        text("Alice will commit")
        alice.schedule { it.commit() }.tryAwait()
        text("Bob will rollback")
        bob.schedule { it.rollback() }

        alice.awaitCompletion()
        bob.awaitCompletion()
    }

    databases.forEach { (name, pool) ->
        isolationLevels.forEach { level ->
            scenario(name, pool, level)
        }
    }
}
