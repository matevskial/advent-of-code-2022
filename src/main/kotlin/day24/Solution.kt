package day24

import readLines
import java.util.HashMap
import kotlin.math.min

fun main() {
    val grid = readLines("/day24/input.txt")
    val lcm = calculateLcm(grid.size - 2, grid[0].length - 2)
    val positions = getPositions(grid, lcm)
    val startPosition = getStartPosition()
    val endPosition = getEndPosition(grid)
    val part1 = solve(Triple(startPosition.first, startPosition.second, 0), endPosition, positions, lcm)
    println("part1: $part1")
    val backToBeginMinutes = solve(Triple(endPosition.first, endPosition.second, part1 % lcm), startPosition, positions, lcm)
    println("back to begin: $backToBeginMinutes")
    val backToEndAgainMinutes = solve(Triple(startPosition.first, startPosition.second, (part1 + backToBeginMinutes) % lcm), endPosition, positions, lcm)
    println("back to end again: $backToEndAgainMinutes")
    println("part2: ${part1 + backToBeginMinutes + backToEndAgainMinutes}")
}

fun getAdjPositions(pair: Pair<Int, Int>): List<Pair<Int, Int>> {
    return Direction.values().map { it.move(pair) }
}

fun solve(
    startState: Triple<Int, Int, Int>,
    endPosition: Pair<Int, Int>,
    positions: Map<Pair<Int, Int>, Set<Int>>,
    lcm: Int
): Int {
    val distances = hashMapOf<Triple<Int, Int, Int>, Int>()
    val queue = ArrayDeque<Triple<Int, Int, Int>>()
    distances[startState] = 0
    queue.addFirst(startState)
    var result = Int.MAX_VALUE
    val x = startState.third
    while (queue.isNotEmpty()) {
        val current = queue.removeLast()
        val currentPosition = Pair(current.first, current.second)
        val currentMinute = distances.getValue(current)
        if (currentPosition == endPosition) {
            result = min(result, currentMinute)
        }
        for(adj in getAdjPositions(Pair(current.first, current.second))) {
            if (adj !in positions) {
                continue
            }
            for(waitMinutes in 0 until lcm) {
                val nextMinute = currentMinute + waitMinutes + 1
                if ((currentMinute + waitMinutes + x) % lcm in positions.getValue(currentPosition)) {
                    break
                }
                val newTriple = Triple(adj.first, adj.second, nextMinute % lcm)
                if ((nextMinute + x) % lcm !in positions.getValue(adj)) {
                    if(newTriple !in distances || nextMinute < distances.getValue(newTriple)) {
                        queue.addFirst(newTriple)
                        distances[newTriple] = nextMinute
                    }
                }
            }
        }
    }

    return result
}

fun getEndPosition(grid: List<String>): Pair<Int, Int> {
    return Pair(grid.size - 1, grid[0].length - 2)
}

fun getStartPosition(): Pair<Int, Int> {
    return Pair(0, 1)
}

fun getPositions(grid: List<String>, lcm: Int): Map<Pair<Int, Int>, Set<Int>> {
    val result = hashMapOf<Pair<Int, Int>, HashSet<Int>>()
    for ((i, row) in grid.withIndex()) {
        for ((j, char) in row.withIndex()) {
            if (char == '.' || isBlizzard(char)) {
                result[Pair(i, j)] = hashSetOf()
            }
        }
    }

    for ((i, row) in grid.withIndex()) {
        for((j, char) in row.withIndex()) {
            if (char == '^') {
                addToPositions(Pair(i, j), result, grid, lcm, Direction.UP)
            } else if(char == 'v') {
                addToPositions(Pair(i, j), result, grid, lcm, Direction.DOWN)
            } else if(char == '<') {
                addToPositions(Pair(i, j), result, grid, lcm, Direction.LEFT)
            } else if(char == '>') {
                addToPositions(Pair(i, j), result, grid, lcm, Direction.RIGHT)
            }
        }
    }
    return result
}

fun addToPositions(
    initialBlizzardPosition: Pair<Int, Int>,
    positions: HashMap<Pair<Int, Int>, java.util.HashSet<Int>>,
    grid: List<String>,
    lcm: Int,
    direction: Direction
) {
    var currentPosition = initialBlizzardPosition
    for (i in 0 until lcm) {
        positions.getValue(currentPosition).add(i)
        currentPosition = direction.moveAndWrap(currentPosition, grid)
    }
}

fun isBlizzard(char: Char): Boolean {
    return char == '>' || char == 'v' || char == '<' || char == '^'
}

fun calculateLcm(n: Int, m: Int): Int {
    return (n * m) / calculateGcd(n, m)
}

fun calculateGcd(n: Int, m: Int): Int {
    if(m == 0) {
        return n
    }

    return calculateGcd(m, n % m)
}

enum class Direction {
    UP {
        override fun move(position: Pair<Int, Int>): Pair<Int, Int> {
            return Pair(position.first - 1, position.second)
        }

        override fun moveAndWrap(position: Pair<Int, Int>, grid: List<String>): Pair<Int, Int> {
            val newPosition = Pair(position.first - 1, position.second)
            if (newPosition.first == 0) {
                return Pair(grid.size - 2, newPosition.second)
            }
            return newPosition
        }
    },
    DOWN {
        override fun move(position: Pair<Int, Int>): Pair<Int, Int> {
            return Pair(position.first + 1, position.second)
        }

        override fun moveAndWrap(position: Pair<Int, Int>, grid: List<String>): Pair<Int, Int> {
            val newPosition = Pair(position.first + 1, position.second)
            if(newPosition.first == grid.size - 1) {
                return Pair(1, newPosition.second)
            }
            return newPosition
        }
    },
    LEFT {
        override fun move(position: Pair<Int, Int>): Pair<Int, Int> {
            return Pair(position.first, position.second - 1)
        }

        override fun moveAndWrap(position: Pair<Int, Int>, grid: List<String>): Pair<Int, Int> {
            val newPosition = Pair(position.first, position.second - 1)
            if(newPosition.second == 0) {
                return Pair(newPosition.first, grid[0].length - 2)
            }
            return newPosition
        }
    },
    RIGHT {
        override fun move(position: Pair<Int, Int>): Pair<Int, Int> {
            return Pair(position.first, position.second + 1)
        }

        override fun moveAndWrap(position: Pair<Int, Int>, grid: List<String>): Pair<Int, Int> {
            val newPosition = Pair(position.first, position.second + 1)
            if(newPosition.second == grid[0].length- 1) {
                return Pair(newPosition.first, 1)
            }
            return newPosition
        }
    };

    abstract fun moveAndWrap(position: Pair<Int, Int>, grid: List<String>): Pair<Int, Int>
    abstract fun move(position: Pair<Int, Int>): Pair<Int, Int>
}
