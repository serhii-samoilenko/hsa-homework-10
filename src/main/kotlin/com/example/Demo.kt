package com.example

import com.example.Report.text
import javax.enterprise.context.ApplicationScoped

/**
 * Isolations & locks demo
 *
 * This demo will use MySQL and PostgreSQL databases.
 *
 * By changing isolation levels and making parallel queries, we will reproduce the main problems of parallel access:
 * - lost update
 * - dirty read
 * - non-repeatable read
 * - phantom read
 */
fun runDemo(helper: Helper) = with(helper) {
    Report.h1("Isolations & locks demo report")
    val mysql = SystemActor("mysql", mysqlConnectionPool)
    val postgres = SystemActor("postgres", postgresConnectionPool)
    text("Creating tables and inserting data")
    text("In MySQL:")
    mysql.execute(
        """CREATE TABLE IF NOT EXISTS persons (
                id   INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(255),
                age  INT
            )
        """.trimIndent(),
        "TRUNCATE TABLE persons",
        "INSERT INTO persons (name, age) VALUES ('Alice', 20), ('Bob', 20), ('Charlie', 20)",
    )
    text("In PostgreSQL:")
    postgres.execute(
        """CREATE TABLE IF NOT EXISTS persons (
                id   SERIAL PRIMARY KEY,
                name VARCHAR(255),
                age  INT
            )
        """.trimIndent(),
        "TRUNCATE TABLE persons",
        "INSERT INTO persons (name, age) VALUES ('Alice', 20), ('Bob', 20), ('Charlie', 20)",
    )

    val databases = listOf("MySQL" to mysqlConnectionPool, "Postgres" to postgresConnectionPool)
    val isolationLevels = listOf("READ COMMITTED", "READ UNCOMMITTED", "REPEATABLE READ", "SERIALIZABLE")

    Report.h2("Lost update")
    text(
        """
        We will use two transactions to update the same row in the database.
        The first transaction will read the value of the row, increment it and write it back.
        The second transaction will read the value of the row, increment it and write it back.
        
        We will run the two transactions in parallel.
        
        We will expect the value of the row to be incremented by 2.
        But we will see that the value of the row is incremented by 1.
        
        This is because the second transaction overwrites the value of the row written by the first transaction.
        
        This is called a lost update.
        """.trimIndent(),
    )
    val scenario = fun(db: String, pool: ConnectionPool, level: String) {
        Report.h3("$db:")
        val alice = UserActor("Alice", pool)
        val bob = UserActor("Bob", pool)
        val system = SystemActor("System", pool)

        text("Both transactions will use the `$level` isolation level:")

        text("Alice starts a transaction and reads the value of the row:")
        alice.begin()
        alice.execute("SET TRANSACTION ISOLATION LEVEL $level")
        val aliceValue = alice.querySingleValue("SELECT age FROM persons WHERE name = 'Alice'") as Int
        text("Alice updates the row:")
        alice.execute("UPDATE persons SET age = ${aliceValue + 1} WHERE name = 'Alice'")

        text("Bob starts a transaction and reads the value of the row:")
        bob.begin()
        bob.execute("SET TRANSACTION ISOLATION LEVEL $level")
        val bobValue = bob.querySingleValue("SELECT age FROM persons WHERE name = 'Alice'") as Int

        text("Bob will update the row")
        bob.executeAsync("UPDATE persons SET age = ${bobValue + 1} WHERE name = 'Alice'")

        text("Alice will commit")
        alice.commitAsync { text("Alice committed now") }

        text("Bob will commit")
        bob.commitAsync { text("Bob committed now") }

        alice.await()
        bob.await()

        text("The value of the row is now:")
        val value = system.querySingleValue("SELECT age FROM persons WHERE name = 'Alice'") as Int
        text("Value: `$value`")
    }
    databases.forEach { (name, pool) ->
        isolationLevels.forEach { level ->
            scenario(name, pool, level)
        }
    }
}

@ApplicationScoped
class Demo(private val helper: Helper) {
    fun start() {
        runDemo(helper)
        Report.writeToFile("REPORT.md")
    }
}
