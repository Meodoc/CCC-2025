package turbo.team

import java.io.File

fun main() {
    val lines = File("test.in").bufferedReader().readText()

    val chunks = lines.split("\n\n")

    println(chunks)
}