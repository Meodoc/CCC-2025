package turbo.team.level5

import java.io.File
import java.io.FileWriter
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

fun isColliding(x: Int, y: Int, asteroidX: Int, asteroidY: Int): Boolean {
    val xRange = x - 2..x + 2
    val yRange = y - 2..y + 2

    return asteroidX in xRange && asteroidY in yRange
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

fun solve(level: Int, stage: String) {
    val fname = "level$level/level${level}_$stage.in"
    val lines = File(fname).readLines().drop(1)
    println(fname)

    val outLines = lines.chunked(2).map { (spaceStationAndTimeLimit, asteroid) ->
        // val paces = line.split(" ").map { it.toInt() }
        val (spaceStation, timeLimit) = spaceStationAndTimeLimit.split(" ")
        val (spaceX, spaceY) = spaceStation.split(",").map { it.toInt() }

        val (asteroidX, asteroidY) = asteroid.split(",").map { it.toInt() }

        val asteroidOffsets = listOf(
            -3 to -3,
            -3 to 3,
            3 to -3,
            3 to 3,
        )

//        val asteroidWayPoints = listOf(
//            asteroidX - 3 to asteroidY - 3,
//            asteroidX - 3 to asteroidY + 3,
//            asteroidX + 3 to asteroidY - 3,
//            asteroidX + 3 to asteroidY + 3,
//        )

        val closestOffsetToStart = asteroidOffsets.map { wayPoint ->
            val globalPos = asteroidX + wayPoint.first to asteroidY + wayPoint.second
            wayPoint to euclidDistance(0 to 0, globalPos)
        }.minBy { it.second }.first

        val closestWayPointToStart = closestOffsetToStart.first + asteroidX to closestOffsetToStart.second + asteroidY

        val neighborWayPoints = listOf(
            closestOffsetToStart.first * -1 to closestOffsetToStart.second,
            closestOffsetToStart.first to closestOffsetToStart.second * -1,
        )

        val closestWayPointToSpaceStation = neighborWayPoints
            .map { it.first + asteroidX to it.second + asteroidY }
            .map { wayPoint ->
                wayPoint to euclidDistance(wayPoint, spaceX to spaceY)
            }.minBy { it.second }.first

        println(closestWayPointToStart)
        println(closestWayPointToSpaceStation)

        // First subs sequence
        val firstPacesX = calcPaces(0, closestWayPointToStart.first, timeLimit.toInt())
        val firstPacesY = calcPaces(0, closestWayPointToStart.second, timeLimit.toInt())

        val firstPaces = padPaces(firstPacesX, firstPacesY)

        val secondPacesX =
            calcPaces(closestWayPointToStart.first, closestWayPointToSpaceStation.first, timeLimit.toInt())
        val secondPacesY =
            calcPaces(closestWayPointToStart.second, closestWayPointToSpaceStation.second, timeLimit.toInt())

        val secondPaces = padPaces(secondPacesX, secondPacesY)

        val thirdPacesX =
            calcPaces(closestWayPointToSpaceStation.first, spaceX, timeLimit.toInt())
        val thirdPacesY =
            calcPaces(closestWayPointToSpaceStation.second, spaceY, timeLimit.toInt())

        val thirdPaces = padPaces(thirdPacesX, thirdPacesY)

        val pacesX = firstPaces.first + secondPaces.first + thirdPaces.first
        val pacesY = firstPaces.second + secondPaces.second + thirdPaces.second

        val timeX = calcTime(pacesX)
        val timeY = calcTime(pacesY)

        assert(timeX < timeLimit.toInt() && timeY < timeLimit.toInt()) {
            println("Time limit $timeLimit exceeded: $timeX, $timeY")
        }


        var positionsX = calcPositions(pacesX)
        var positionsY = calcPositions(pacesY)

        val maxPositionLen = max(positionsX.size, positionsY.size)

        positionsX = positionsX.rightPad(maxPositionLen, positionsX.last())
        positionsY = positionsY.rightPad(maxPositionLen, positionsX.last())

        val out = "${pacesX.joinToString(" ")}\n${pacesY.joinToString(" ")}"
        println(out)


        println(positionsX)
        println(positionsY)

        positionsX.zip(positionsY).forEach { (x, y) ->
            assert(!isColliding(x, y, asteroidX, asteroidY)) {
                "Collision: $x, $y"
            }
        }

        println()
        out

        // Second subs sequence

        // val out = "$pacesX\n$pacesY"
        // println(out)

//        var positionsX = calcPositions(pacesX)
//        var positionsY = calcPositions(pacesY)
//
//        val maxLen = max(positionsX.size, positionsY.size)
//        positionsX = positionsX.rightPad(maxLen, positionsX.last())
//        positionsY = positionsY.rightPad(maxLen, positionsY.last())
//
//        println(positionsX)
//        println(positionsY)
//        println()

        // out
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
    val level = 5
    val stages = listOf("1_small", "2_large")
    // val stages = listOf("0_example")

    stages.forEach { stage ->
        solve(level, stage)
    }
}