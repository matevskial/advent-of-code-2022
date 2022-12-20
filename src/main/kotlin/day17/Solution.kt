package day17

import readLines
import java.util.HashSet

fun main() {
    val jetPattern = readLines("/day17/input.txt")[0]
    val numberOfTimesPart1 = 2022
    val maxHeight = simulate(jetPattern, numberOfTimesPart1)
    println("part1: ${maxHeight.maxY}")

    val numberOfTimesPart2 = 1000000000000L
    val pattern = getPattern(jetPattern)
    println("part2: ${part2(numberOfTimesPart2, pattern, jetPattern)}")
}

fun getPattern(jetPattern: String): Pattern {
    var currentNumberOfTimes = 1000
    var pattern = simulate(jetPattern, currentNumberOfTimes)
    while(pattern.isInvalid()) {
        currentNumberOfTimes *= 2
        pattern = simulate(jetPattern, currentNumberOfTimes)
    }
    return pattern
}

fun part2(n: Long, pattern: Pattern, jetPattern: String): Long {
    val k = (n - pattern.startOfPattern) / pattern.numberOfRocksDelta
    val nn = pattern.startOfPattern + k * pattern.numberOfRocksDelta
    val remN = n - nn
    val ynn = pattern.maxYAtStartOfPattern + k * pattern.maxYDelta
    val yrem = simulate(jetPattern, (pattern.startOfPattern + remN).toInt()).maxY
    val y = ynn + (yrem - pattern.maxYAtStartOfPattern)
    return y
}

fun simulate(jetPattern: String, numberOfTimes: Int): Pattern {
    val rockPatterns = "-+LI.".toList()
    var currentJetIndex = 0
    var currentRockIndex = 0

    val towerOfRocks = hashSetOf<Pair<Int, Int>>()
    var maxY = addRock(towerOfRocks, getRockPatternPositions("-------", Pair(0, 0)), 0)
    val xRange = 0..6

    val pattern = Pattern(-1, -1, -1, -1, 0)
    var isStartOfPatternFound = false
    var isStartOfSecondPatternFound = false
    var rockIndexPattern = -1
    for(i in 1L..numberOfTimes) {
        var rock = getRockPatternPositions(rockPatterns[currentRockIndex].toString(), Pair(2, maxY + 4))

        var count = 0
        while(true) {
            count++
            if(currentJetIndex == 0 && i > 1) {
                if(isStartOfPatternFound && currentRockIndex == rockIndexPattern) {
                    pattern.numberOfRocksDelta = i - pattern.startOfPattern
                    isStartOfSecondPatternFound = true
                    rockIndexPattern = -1
                } else if(!isStartOfPatternFound) {
                    isStartOfPatternFound = true
                    rockIndexPattern = currentRockIndex
                    pattern.startOfPattern = i
                }

            }

            rock = pushRockByJet(rock, jetPattern[currentJetIndex], xRange, towerOfRocks)

            currentJetIndex++
            if(currentJetIndex == jetPattern.length) {
                currentJetIndex = 0
            }

            val newRock = moveRockDown(rock)

            if(isRockAtClashingWithTowerOfRock(newRock, towerOfRocks)) {
                break
            }
            rock = newRock
        }

        maxY = addRock(towerOfRocks, rock, maxY)

        currentRockIndex++
        if(currentRockIndex == rockPatterns.size) {
            currentRockIndex = 0
        }

        if(isStartOfPatternFound && pattern.maxYAtStartOfPattern == -1L) {
            pattern.maxYAtStartOfPattern = maxY.toLong()
        }
        if(isStartOfSecondPatternFound && pattern.maxYDelta == -1L) {
            pattern.maxYDelta = maxY.toLong() - pattern.maxYAtStartOfPattern
        }
    }

    pattern.maxY = maxY.toLong()
    return pattern
}

fun moveRockDown(rock: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
    return rock.map { Pair(it.first, it.second - 1) }
}

fun pushRockByJet(
    rock: List<Pair<Int, Int>>,
    jet: Char,
    xRange: IntRange,
    towerOfRocks: HashSet<Pair<Int, Int>>
): List<Pair<Int, Int>> {
    val newRock = if(jet == '<') {
        rock.map { Pair(it.first - 1, it.second) }
    } else {
        rock.map { Pair(it.first + 1, it.second) }
    }

    val isNewRockInValidPosition = newRock.none { it in towerOfRocks || it.first !in xRange }
    return if(isNewRockInValidPosition) {
        newRock
    } else {
        rock
    }
}

fun isRockAtClashingWithTowerOfRock(rock: List<Pair<Int, Int>>, towerOfRocks: HashSet<Pair<Int, Int>>): Boolean {
    for (position in rock) {
        if (Pair(position.first, position.second) in towerOfRocks) {
            return true
        }
    }
    return false
}

fun getRockPatternPositions(rockPattern: String, rockPosition: Pair<Int, Int>): List<Pair<Int, Int>> {
    var patternPositions = arrayListOf<Pair<Int, Int>>()
    when(rockPattern) {
        "-------" -> patternPositions = arrayListOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(3, 0), Pair(4, 0), Pair(5, 0), Pair(6, 0))
        "-" -> patternPositions = arrayListOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(3, 0))
        "+" -> patternPositions = arrayListOf(Pair(1, 0), Pair(0, 1), Pair(1, 1), Pair(2, 1), Pair(1, 2))
        "L" -> patternPositions = arrayListOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(2, 1), Pair(2, 2))
        "I" -> patternPositions = arrayListOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(0, 3))
        "." -> patternPositions = arrayListOf(Pair(0, 0), Pair(1, 0), Pair(0, 1), Pair(1, 1))
    }
    return patternPositions.map { Pair(it.first + rockPosition.first, it.second + rockPosition.second) }
}

fun addRock(towerOfRocks: HashSet<Pair<Int, Int>>, rock: List<Pair<Int, Int>>, oldMaxY: Int): Int {
    var maxY = oldMaxY
    for (position in rock) {
        towerOfRocks.add(position)
        maxY = maxOf(maxY, position.second)
    }
    return maxY
}

data class Pattern(
    var numberOfRocksDelta: Long,
    var maxYDelta: Long,
    var startOfPattern: Long,
    var maxYAtStartOfPattern: Long,
    var maxY: Long
) {
    fun isInvalid(): Boolean {
        return numberOfRocksDelta == -1L || maxYDelta == -1L || startOfPattern == -1L || maxYAtStartOfPattern == -1L
    }
}