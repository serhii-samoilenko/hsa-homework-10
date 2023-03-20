package com.example.scenarios

import com.example.UserActor
import com.example.util.ConnectionPool
import com.example.util.Helper
import com.example.util.Report

fun nonRepeatableReadScenario(helper: Helper, r: Report) = with(helper) {
    val databases = listOf("MySQL" to mysqlConnectionPool, "Postgres" to postgresConnectionPool)
    val isolationLevels = listOf("READ UNCOMMITTED", "READ COMMITTED", "REPEATABLE READ", "SERIALIZABLE")

    r.h2("Non-repeatable read")
    r.text(
        """
        A non-repeatable read occurs when a transaction retrieves a row twice and that row is updated by another transaction that is committed in between.
        
        We will run two transactions in parallel.
        The first transaction will read the value of the row.
        The second transaction will increment the value of the row and commit.
        Then the first transaction will read the value of the row again.
        
        We will use different isolation levels to see how they affect the result.
        """.trimIndent(),
    )

    val scenario = fun(db: String, pool: ConnectionPool, level: String) {
        r.h3("$db with $level:")

        val alice = UserActor("Alice", pool, r)
        val bob = UserActor("Bob", pool, r)

        alice.begin()
        alice.execute("SET TRANSACTION ISOLATION LEVEL $level")

        r.text("Alice will select the value of the row")
        val aliceValue = alice.querySingleValue("SELECT amount FROM accounts WHERE name = 'Charlie'") as Int

        r.line()
        r.text("Bob will select the value of the row, increment it and commit")
        bob.begin()
        bob.schedule {
            it.execute("UPDATE accounts SET amount = ${aliceValue + 10} WHERE name = 'Charlie'")
            it.commit()
        }.tryAwait()
        r.line()

        r.text("Alice will AGAIN select the value of the row")
        val value = alice.querySingleValue("SELECT amount FROM accounts WHERE name = 'Charlie'") as Int

        r.text("Alice will commit")
        alice.commit()
        r.line()

        alice.awaitCompletion()
        bob.awaitCompletion()
        r.text("Value: `$value`")
    }

    databases.forEach { (name, pool) ->
        isolationLevels.forEach { level ->
            scenario(name, pool, level)
        }
    }
}
