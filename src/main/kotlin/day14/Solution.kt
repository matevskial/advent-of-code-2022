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
    for(sandY in sandSource.second..cave.getMaxRockY()) {
        val unitOfSandCurrentPosition = Pair(sandX, sandY)
        val unitOfSandDownPosition = Pair(sandX, sandY + 1)
        val unitOfSandDiagonallyDownLeftPosition = Pair(sandX - 1, sandY + 1)
        val unitOfSandDiagonallyDownRightPosition = Pair(sandX + 1, sandY + 1)

        if(cave.isAir(unitOfSandDownPosition)) {
            continue
        } else if(cave.isAir(unitOfSandDiagonallyDownLeftPosition)) {
            sandX--
        } else if(cave.isAir(unitOfSandDiagonallyDownRightPosition)) {
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
    val rocks = hashSetOf<Pair<Int, Int>>()
    var maxY = Int.MIN_VALUE
    for(line in input) {
        val coordinateTokens = line.split(" -> ")
        for(i in 0 until coordinateTokens.size - 1) {
            val rockPathStart = Pair(coordinateTokens[i].split(",")[0].toInt(), coordinateTokens[i].split(",")[1].toInt())
            val rockPathEnd = Pair(coordinateTokens[i + 1].split(",")[0].toInt(), coordinateTokens[i + 1].split(",")[1].toInt())
            val rockPath = RockPath(rockPathStart, rockPathEnd)
            if(rockPath.isHorizontal()) {
                val y = rockPath.start.second
                for(x in rockPath.xRange()) {
                    rocks.add(Pair(x, y))
                }
            } else if (rockPath.isVertical()) {
                val x = rockPath.start.first
                for(y in rockPath.yRange()) {
                    rocks.add(Pair(x, y))
                }
            }
            maxY = maxOf(maxY, rockPathStart.second, rockPathEnd.second)

        }
    }
    return Cave(rocks, maxY)
}

class Cave(private val rocks: Set<Pair<Int, Int>>, private val maxRockY: Int) {
    private val unitOfSands = hashSetOf<Pair<Int, Int>>()
    private var isPart2 = false

    fun getMaxRockY(): Int {
        return if(isPart2) {
            maxRockY + 2
        } else {
            maxRockY
        }
    }

    fun isAir(position: Pair<Int, Int>): Boolean {
        if(isPart2 && position.second == getMaxRockY()) {
            return false
        }
        return position !in rocks && position !in unitOfSands
    }

    fun addUnitOfSandAtRest(unitOfSandPosition: Pair<Int, Int>) {
        unitOfSands.add(unitOfSandPosition)
    }

    fun setupPart2() {
        unitOfSands.clear()
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
