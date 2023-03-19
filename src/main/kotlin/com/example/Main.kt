package com.example

import io.quarkus.runtime.Quarkus
import io.quarkus.runtime.QuarkusApplication
import io.quarkus.runtime.annotations.QuarkusMain

@QuarkusMain
class Main : QuarkusApplication {
    override fun run(vararg args: String): Int {
        println("Hello " + args[1])
        return 0
    }
}

object JavaMain {
    @JvmStatic
    fun main(vararg args: String) {
        Quarkus.run(Main::class.java, *args)
    }
}
