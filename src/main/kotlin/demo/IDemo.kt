package demo

import helpers.Colors
import helpers.ColorsHelper.makeColored
import kotlin.system.measureTimeMillis

interface IDemo {
    val title: String
    suspend fun execute()

    suspend fun run() {
        println(title.makeColored(Colors.BACKGROUND_GREEN, Colors.FOREGROUND_BLACK))
        val ms = measureTimeMillis {
            execute()
        }
        println("DONE, ms\t${ms.toString().makeColored(Colors.BACKGROUND_BRIGHT_YELLOW, Colors.FOREGROUND_BLACK)}")
        println()
    }
}
