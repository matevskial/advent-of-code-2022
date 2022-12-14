package day14

import readLines
import java.lang.Integer.max
import java.lang.Integer.min

fun main() {
    val cave = parseCave(readLines("/day14/input.txt"))
    var unitsOfSandAtRest = simulateFallingSand(cave, Pair(500, 0))
    println("part1: $unitsOfSandAtRest")
    cave.setupPart2()
    unitsOfSandAtRest = simulateFallingSand(cave, Pair(500, 0))
    println("part2: $unitsOfSandAtRest")
}

fun simulateFallingSand(cave: Cave, sandSource: Pair<Int, Int>): Int {
    var isLastUnitOfSandAtRest = true
    while (isLastUnitOfSandAtRest) {
        isLastUnitOfSandAtRest = simulateFallingUnitOfSand(cave, sandSource)
    }
    return cave.getUnitsOfSandAtRest()
}

fun simulateFallingUnitOfSand(cave: Cave, sandSource: Pair<Int, Int>): Boolean {
    var sandX = sandSource.first
    for(sandY in sandSource.second..cave.maxRockY) {
        val unitOfSandCurrentPosition = Pair(sandX, sandY)
        val unitOfSandDownPosition = Pair(sandX, sandY + 1)
        val unitOfSandDiagonallyDownLeftPosition = Pair(sandX - 1, sandY + 1)
        val unitOfSandDiagonallyDownRightPosition = Pair(sandX + 1, sandY + 1)

        if(cave.canUnitOfSandMoveToPosition(unitOfSandDownPosition)) {
            continue
        } else if(cave.canUnitOfSandMoveToPosition(unitOfSandDiagonallyDownLeftPosition)) {
            sandX--
        } else if(cave.canUnitOfSandMoveToPosition(unitOfSandDiagonallyDownRightPosition)) {
            sandX++
        } else if(unitOfSandCurrentPosition != sandSource){
            cave.addUnitOfSandAtRest(unitOfSandCurrentPosition)
            return true
        } else if(unitOfSandCurrentPosition == sandSource) {
            cave.addUnitOfSandAtRest(unitOfSandCurrentPosition)
            return false
        }
    }
    return false
}

fun parseCave(input: List<String>): Cave {
    val cave = Cave()
    var maxY = Int.MIN_VALUE
    var minY = Int.MAX_VALUE
    var minX = Int.MAX_VALUE
    var maxX = Int.MIN_VALUE
    for(line in input) {
        val coordinateTokens = line.split(" -> ")
        for(i in 0 until coordinateTokens.size - 1) {
            val rockPathStart = Pair(coordinateTokens[i].split(",")[0].toInt(), coordinateTokens[i].split(",")[1].toInt())
            val rockPathEnd = Pair(coordinateTokens[i + 1].split(",")[0].toInt(), coordinateTokens[i + 1].split(",")[1].toInt())
            cave.addRockPath(RockPath(rockPathStart, rockPathEnd))
            minY = minOf(minY, rockPathStart.second, rockPathEnd.second)
            maxY = maxOf(maxY, rockPathStart.second, rockPathEnd.second)
            minX = minOf(minX, rockPathStart.first, rockPathEnd.first)
            maxX = maxOf(maxX, rockPathStart.first, rockPathEnd.first)
        }
    }
    println("min x $minX, max x $maxX")
    println("min y $minY, max y $maxY")
    cave.maxRockY = maxY
    return cave
}

class Cave {
    private val rockPaths = arrayListOf<RockPath>()
    private val unitOfSands = arrayListOf<Pair<Int, Int>>()
    private var isPart2 = false
    var maxRockY = -1

    fun canUnitOfSandMoveToPosition(unitOfSandPosition: Pair<Int, Int>): Boolean {
        return isAir(unitOfSandPosition)
    }

    private fun isAir(position: Pair<Int, Int>): Boolean {
        if(isPart2 && position.second == maxRockY) {
            return false
        }
        if(position in unitOfSands) {
            return false
        }
        for (path in rockPaths) {
            if(path.isHorizontal() && position.first in path.xRange() && position.second == path.start.second) {
                return false
            } else if(path.isVertical() && position.second in path.yRange() && position.first == path.start.first) {
                return false
            }
        }
        return true
    }

    fun addUnitOfSandAtRest(unitOfSandPosition: Pair<Int, Int>) {
        unitOfSands.add(unitOfSandPosition)
    }

    fun addRockPath(rockPath: RockPath) {
        rockPaths.add(rockPath)
    }

    fun setupPart2() {
        unitOfSands.clear()
        maxRockY += 2
        isPart2 = true
    }

    fun getUnitsOfSandAtRest(): Int {
        return unitOfSands.size
    }
}

data class RockPath(val start: Pair<Int, Int>, val end: Pair<Int, Int>) {

    fun isHorizontal(): Boolean {
        return start.second == end.second
    }

    fun isVertical(): Boolean {
        return start.first == end.first
    }

    fun xRange(): IntRange {
        return min(start.first, end.first)..max(start.first, end.first)
    }

    fun yRange(): IntRange {
        return min(start.second, end.second)..max(start.second, end.second)
    }
}
