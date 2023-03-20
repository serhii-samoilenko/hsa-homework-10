package com.example.util

import java.io.File

class Report(
    private val fileName: String,
) {
    private var data: String = ""

    @Synchronized
    fun text(text: String) {
        "$text\n".also { data += it + "\n" }.also { println(it) }
    }

    fun h1(text: String) {
        text("# $text")
    }

    fun h2(text: String) {
        text("## $text")
    }

    fun h3(text: String) {
        text("### $text")
    }

    fun code(text: String) {
        text("`$text`")
    }

    fun line() {
        text("---")
    }

    fun sql(sql: String) {
        sql(listOf(sql))
    }

    fun sql(sql: List<String>) {
        val block = "```sql\n${sql.joinToString("\n")}\n```"
        text(block)
    }

    fun sql(sql: List<String>, actor: String) {
        sql(sql, actor, null)
    }

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
    fun writeToFile() {
        // Get current directory
        val currentDir = File("").absolutePath
        val file = File(fileName)
        file.writeText(data)
        println("Wrote report to $currentDir/$fileName")
    }

    @Synchronized
    fun clear() {
        data = ""
    }
}
