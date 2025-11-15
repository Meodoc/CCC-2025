package turbo.team

//import java.io.File
//import java.io.FileWriter
//
//fun solve1(level: Int, stage: String) {
//    val fname = "level$level/level${level}_$stage.in"
//    val lines = File(fname).readLines().drop(1)
//    val sums = lines.map { line -> line.split(" ").map { it.toInt() }.sum() }
//    println(lines)
//    println(sums)
//    FileWriter(fname.replace(".in", ".out")).use {writer ->
//        writer.write(sums.joinToString("\n"))
//    }
//}
//
//
//fun main() {
//    val level = 1
//    val stages = listOf("1_small", "2_large")
//
//    stages.forEach { stage ->
//
//        solve1(level, stage)
//    }
//}