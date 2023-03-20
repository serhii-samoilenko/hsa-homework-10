package com.example

import com.example.scenarios.dirtyReadScenario
import com.example.scenarios.lostUpdateScenario
import com.example.util.Helper
import com.example.util.Report
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
    val r = Report("REPORT.md")
    r.h1("Isolations & locks demo report")
    val mysql = SystemActor("mysql", mysqlConnectionPool, r)
    val postgres = SystemActor("postgres", postgresConnectionPool, r)
    r.text("Creating tables and inserting data")
    r.text("In MySQL:")
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
    r.text("In PostgreSQL:")
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

    r.h2("[Lost update scenario](LOST_UPDATE.md)")
    r.h2("[Dirty read scenario](DIRTY_READ.md)")
    r.writeToFile()

    var scenarioReport = Report("LOST_UPDATE.md")
    lostUpdateScenario(helper, scenarioReport)
    scenarioReport.writeToFile()

    scenarioReport = Report("DIRTY_READ.md")
    dirtyReadScenario(helper, scenarioReport)
    scenarioReport.writeToFile()
}

@ApplicationScoped
class Demo(private val helper: Helper) {
    fun start() {
        runDemo(helper)
    }
}
