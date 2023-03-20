package com.example.scenarios

import com.example.SystemActor
import com.example.UserActor
import com.example.util.ConnectionPool
import com.example.util.Helper
import com.example.util.Report
import kotlin.random.Random

fun phantomReadScenario(helper: Helper, r: Report) = with(helper) {
    val databases = listOf("MySQL" to mysqlConnectionPool, "Postgres" to postgresConnectionPool)
    val isolationLevels = listOf("READ UNCOMMITTED", "READ COMMITTED", "REPEATABLE READ", "SERIALIZABLE")

    r.h2("Phantom read")
    r.text(
        """
        A phantom read occurs when a transaction retrieves a set of rows twice and new rows are inserted into or removed from that set 
        by another transaction that is committed in between.
        
        We will run two transactions in parallel.
        The first transaction will query rows matching a criteria.
        The second transaction will insert a new row matching the criteria and commit.
        Then the first transaction will repeat the query.
        
        We will use different isolation levels to see how they affect the result.
        """.trimIndent(),
    )

    val scenario = fun(db: String, pool: ConnectionPool, level: String) {
        r.h3("$db with $level:")

        val alice = UserActor("Alice", pool, r)
        val bob = UserActor("Bob", pool, r)
        val system = SystemActor("System", pool, r)

        r.text("System will make one row match the criteria")
        system.execute("Update accounts set amount = 100 where name = 'Dave'")

        alice.begin()
        alice.execute("SET TRANSACTION ISOLATION LEVEL $level")

        r.text("Alice will perform a query")
        alice.queryRowValues("SELECT name FROM accounts WHERE amount >= 100")

        r.line()
        r.text("Bob will insert a new row matching the criteria and commit")
        val newName = "User"
        bob.begin()
        bob.schedule {
            it.execute("INSERT INTO accounts (name, amount) VALUES ('$newName', 100)")
            it.commit()
        }.tryAwait()
        r.line()

        r.text("Alice will AGAIN perform a query")
        @Suppress("UNCHECKED_CAST")
        val aliceValues = alice.queryRowValues("SELECT name FROM accounts WHERE amount >= 100") as List<String>

        r.text("Alice will commit")
        alice.commit()
        r.line()

        alice.awaitCompletion()
        bob.awaitCompletion()
        system.execute("DELETE FROM accounts WHERE name = '$newName'")

        r.text("Values: `$aliceValues`")
    }

    databases.forEach { (name, pool) ->
        isolationLevels.forEach { level ->
            scenario(name, pool, level)
        }
    }
}
