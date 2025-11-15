package turbo.team

//import java.io.File
//import java.io.FileWriter
//import kotlin.math.abs
//
//fun solve3(level: Int, stage: String) {
//    val fname = "level$level/level${level}_$stage.in"
//    val lines = File(fname).readLines().drop(1)
//    println(fname)
//    val outLines = lines.map { line ->
//        val paces = line.split(" ").map { it.toInt() }
//        val (spaceStation, timeLimit) = line.split(" ").map { it.toInt() }
//
//        val sign = if (spaceStation < 0) -1 else 1
//
//        val sequence = List(abs(spaceStation) + 2) { 1 }.toMutableList()
//
//        sequence[0] = 0
//        sequence[sequence.size - 1] = 0
//
//
//        for (i in 1..5) {
//            val cond = if (sequence.size % 2 == 0) {
//                i >= sequence.size / 2
//            } else {
//                i > sequence.size / 2
//            }
//            if (cond) {
//                break
//            }
//
//            sequence[i] = 6 - i
//            sequence[sequence.size - 1 - i] = 6 - i
//        }
//
//        sequence.map { it * sign }.joinToString(" ")
//    }
//
//    FileWriter(fname.replace(".in", ".out")).use { writer ->
//        writer.write(outLines.joinToString("\n"))
//    }
//}
//
//
//fun main() {
//    val level = 3
//    val stages = listOf("1_small", "2_large")
//    // val stages = listOf("0_example")
//
//    stages.forEach { stage ->
//        solve3(level, stage)
//    }
//}