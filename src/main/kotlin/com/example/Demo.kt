package com.example

import com.example.scenarios.lostUpdateScenario
import com.example.util.Helper
import com.example.util.Report
import com.example.util.Report.h1
import com.example.util.Report.text
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
    h1("Isolations & locks demo report")
    val mysql = SystemActor("mysql", mysqlConnectionPool)
    val postgres = SystemActor("postgres", postgresConnectionPool)
    text("Creating tables and inserting data")
    text("In MySQL:")
    mysql.execute(
        """
            CREATE TABLE IF NOT EXISTS persons (
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
        """
            CREATE TABLE IF NOT EXISTS persons (
                id   SERIAL PRIMARY KEY,
                name VARCHAR(255),
                age  INT
            )
        """.trimIndent(),
        "TRUNCATE TABLE persons",
        "INSERT INTO persons (name, age) VALUES ('Alice', 20), ('Bob', 20), ('Charlie', 20)",
    )

    Report.h2("[Lost update scenario](LOST_UPDATE.md)")
    Report.writeToFile("REPORT.md")

    Report.clear()
    lostUpdateScenario(helper)
    Report.writeToFile("LOST_UPDATE.md")
}

@ApplicationScoped
class Demo(private val helper: Helper) {
    fun start() {
        runDemo(helper)
    }
}
