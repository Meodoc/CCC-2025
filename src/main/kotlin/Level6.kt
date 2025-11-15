package turbo.team

import java.io.File
import java.io.FileWriter
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt


fun isColliding(pacesX: List<Int>, pacesY: List<Int>, asteroidX: Int, asteroidY: Int): Boolean {
    fun isCollidingInternal(x: Int, y: Int, asteroidX: Int, asteroidY: Int): Boolean {
        val xRange = x - 2..x + 2
        val yRange = y - 2..y + 2

        return asteroidX in xRange && asteroidY in yRange
    }

    var positionsX = calcPositions(pacesX)
    var positionsY = calcPositions(pacesY)

    val maxPositionLen = max(positionsX.size, positionsY.size)

    positionsX = positionsX.rightPad(maxPositionLen, positionsX.last())
    positionsY = positionsY.rightPad(maxPositionLen, positionsX.last())

    positionsX.zip(positionsY).forEach { (x, y) ->
        if (isCollidingInternal(x, y, asteroidX, asteroidY)) {
            return true
        }
    }

    return false
}


fun isInsideTimeLimit(pacesX: List<Int>, pacesY: List<Int>, timeLimit: Int): Boolean {
    val timeX = calcTime(pacesX)
    val timeY = calcTime(pacesY)

    if (timeX >= timeLimit.toInt() || timeY >= timeLimit.toInt()) {
        return false
    }

    return true
}

fun List<Int>.rightPad(maxLen: Int, padValue: Int = 0): List<Int> {
    return this + List(maxLen - this.size) { padValue }
}

fun calcPaces(startPos: Int, targetPos: Int, timeLimit: Int): List<Int> {
    val delta = targetPos - startPos

    val sign = if (delta < 0) -1 else 1
    val paces = List(abs(delta) + 2) { 1 }.toMutableList()

    paces[0] = 0
    paces[paces.size - 1] = 0

    for (i in 1..5) {
        val cond = if (paces.size % 2 == 0) {
            i >= paces.size / 2
        } else {
            i > paces.size / 2
        }
        if (cond) {
            break
        }

        paces[i] = 6 - i
        paces[paces.size - 1 - i] = 6 - i
    }

    return if (paces.size == 2) listOf(0) else paces.map { it * sign }
}

fun calcPositions(paces: List<Int>): List<Int> {
    var pos = 0
    return buildList<Int> {
        paces.forEach { pace ->
            val sign = if (pace < 0) -1 else 1
            if (pace != 0) {
                for (ignored in 0..<abs(pace)) {
                    add(pos)
                }
                pos += sign
            } else {
                add(pos)
            }
        }
    }
}


fun euclidDistance(a: Pair<Int, Int>, b: Pair<Int, Int>): Double {
    return sqrt((a.first - b.first).toDouble().pow(2) + (a.second - b.second).toDouble().pow(2))
}

fun calcPath(wayPoints: List<Pair<Int, Int>>): Pair<List<Int>, List<Int>> {
    var curPos = 0 to 0

    val xPath = mutableListOf<Int>()
    val yPath = mutableListOf<Int>()

    for ((x, y) in wayPoints) {
        val xPaces = calcPaces(curPos.first, x, 0)
        val yPaces = calcPaces(curPos.second, y, 0)

        val paces = padPaces(xPaces, yPaces)

        xPath += paces.first
        yPath += paces.second

        curPos = x to y
    }

    return xPath to yPath
}


fun solveLine(spaceStationAndTimeLimit: String, asteroid: String): Pair<List<Int>, List<Int>> {
    val (spaceStation, timeLimit) = spaceStationAndTimeLimit.split(" ")
    val (spaceX, spaceY) = spaceStation.split(",").map { it.toInt() }
    val spacePos = spaceX to spaceY

    val (asteroidX, asteroidY) = asteroid.split(",").map { it.toInt() }

    val asteroidOffsets = listOf(
        -3 to -3,
        -3 to 3,
        3 to -3,
        3 to 3,
    )

    println(spaceStationAndTimeLimit)
    println(asteroid)
    println()

    val directPath = calcPath(listOf(spacePos))

    if (!isColliding(directPath.first, directPath.second, asteroidX, asteroidY) &&
        isInsideTimeLimit(directPath.first, directPath.second, timeLimit.toInt())
    ) {
        return directPath
    }


    asteroidOffsets.forEach { asteroidOffset ->
        val path = calcPath(
            listOf(
                asteroidOffset.first + asteroidX to asteroidOffset.second + asteroidY,
                spacePos
            ),
        )

        if (!isColliding(path.first, path.second, asteroidX, asteroidY) &&
            isInsideTimeLimit(path.first, path.second, timeLimit.toInt())
        ) {
            return path
        }
    }

    asteroidOffsets.forEach { asteroidOffset1 ->
        asteroidOffsets.forEach { asteroidOffset2 ->
            val path = calcPath(
                listOf(
                    asteroidOffset1.first + asteroidX to asteroidOffset1.second + asteroidY,
                    asteroidOffset2.first + asteroidX to asteroidOffset2.second + asteroidY,
                    spacePos
                ),
            )

            if (!isColliding(path.first, path.second, asteroidX, asteroidY) &&
                isInsideTimeLimit(path.first, path.second, timeLimit.toInt())
            ) {
                return path
            }
        }
    }

    throw IllegalStateException("No path found")
}


fun solve6(level: Int, stage: String) {
    val fname = "level$level/level${level}_$stage.in"
    val lines = File(fname).readLines().drop(1)
    println(fname)

    val outLines = lines.chunked(2).map { (spaceStationAndTimeLimit, asteroid) ->
        // val paces = line.split(" ").map { it.toInt() }
        val result = solveLine(spaceStationAndTimeLimit, asteroid)
        "${result.first.joinToString(separator = " ")}\n${result.second.joinToString(separator = " ")}}"
    }

    FileWriter(fname.replace(".in", ".out")).use { writer ->
        writer.write(outLines.joinToString("\n\n"))
    }
}

fun calcTime(paces: List<Int>): Int {
    return paces.map { pace ->
        if (pace == 0) 1 else pace
    }.map { abs(it) }.sum()
}

fun padPaces(pacesX: List<Int>, pacesY: List<Int>): Pair<List<Int>, List<Int>> {
    val timeX = calcTime(pacesX)
    val timeY = calcTime(pacesY)

    val maxTime = max(timeX, timeY)

    val pacesXOut = pacesX + List(maxTime - timeX) { 0 }
    val pacesYOut = pacesY + List(maxTime - timeY) { 0 }

    return pacesXOut to pacesYOut
}

fun main() {
    val level = 6
    val stages = listOf("1_small", "2_large")
    // val stages = listOf("0_example")

    stages.forEach { stage ->
        solve6(level, stage)
    }
}