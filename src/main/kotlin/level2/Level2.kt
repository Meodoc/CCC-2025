package turbo.team.level2

import java.io.File
import java.io.FileWriter
import kotlin.math.abs

fun solve(level: Int, stage: String) {
    val fname = "level$level/level${level}_$stage.in"
    val lines = File(fname).readLines().drop(1)
    println(fname)
    val outLines = lines.map { line ->
        val paces = line.split(" ").map { it.toInt() }
        val positions = paces.map { pace ->
            if (pace < 0) {
                -1
            } else if (pace == 0) {
                0
            } else {
                +1
            }
        }.sum()

        val times = paces.map { pace ->
            if (pace == 0) {
                1
            } else {
                abs(pace)
            }
        }.sum()

        "$positions $times"
    }

    println(outLines)

    FileWriter(fname.replace(".in", ".out")).use { writer ->
        writer.write(outLines.joinToString("\n"))
    }
}

fun main() {
    val level = 2
    val stages = listOf("1_small", "2_large")
    // val stages = listOf("0_example")

    stages.forEach { stage ->
        solve(level, stage)
    }
}