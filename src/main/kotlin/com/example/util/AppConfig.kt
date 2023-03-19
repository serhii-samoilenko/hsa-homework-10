package com.example.util

import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithName

@ConfigMapping(prefix = "app")
interface AppConfig {
    @WithName("postgres")
    fun postgres(): DbConfig

    @WithName("mysql")
    fun mysql(): DbConfig
}

interface DbConfig {
    @WithName("jdbc-url")
    fun jdbcUrl(): String

    @WithName("username")
    fun username(): String

    @WithName("password")
    fun password(): String

    @WithName("connection-pool-size")
    fun connectionPoolSize(): Int
}
