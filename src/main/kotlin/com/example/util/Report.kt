package com.example.util

import java.io.File

object Report {
    private var data: String = ""

    @Synchronized
    fun text(text: String) {
        "$text\n".also { data += it + "\n" }.also { println(it) }
    }

    @Synchronized
    fun h1(text: String) {
        text("# $text")
    }

    @Synchronized
    fun h2(text: String) {
        text("## $text")
    }

    @Synchronized
    fun h3(text: String) {
        text("### $text")
    }

    @Synchronized
    fun code(text: String) {
        text("`$text`")
    }

    @Synchronized
    fun sql(sql: String) {
        sql(listOf(sql))
    }

    @Synchronized
    fun sql(sql: List<String>) {
        val block = "```sql\n${sql.joinToString("\n")}\n```"
        text(block)
    }

    @Synchronized
    fun sql(sql: List<String>, actor: String) {
        sql(sql, actor, null)
    }

    @Synchronized
    fun sql(sql: List<String>, actor: String, result: String?) {
        var block = "```sql\n"
        if (actor.isNotBlank()) {
            block += "-- $actor:\n"
        }
        block += sql.joinToString("\n")
        if (result?.isNotBlank() == true) {
            block += "\n-- Result: $result"
        }
        block += "\n```"
        text(block)
    }

    @Synchronized
    fun writeToFile(fileName: String) {
        // Get current directory
        val currentDir = File("").absolutePath
        val file = File(fileName)
        file.writeText(data)
        println("Wrote report to $currentDir/$fileName")
    }

    fun clear() {
        data = ""
    }
}
