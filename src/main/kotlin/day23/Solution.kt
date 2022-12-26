package day23

import readLines
import java.util.HashMap
import kotlin.math.abs

val directionOrders = arrayOf(
    arrayOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST),
    arrayOf(Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.NORTH),
    arrayOf(Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH),
    arrayOf(Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.WEST),
)

val adjFor = mapOf(
    Direction.NORTH to arrayOf(Direction.NORTH, Direction.NORTH_WEST, Direction.NORTH_EAST),
    Direction.SOUTH to arrayOf(Direction.SOUTH, Direction.SOUTH_EAST, Direction.SOUTH_WEST),
    Direction.WEST to arrayOf(Direction.WEST, Direction.NORTH_WEST, Direction.SOUTH_WEST),
    Direction.EAST to arrayOf(Direction.EAST, Direction.NORTH_EAST, Direction.SOUTH_EAST)
)

fun main() {
    val input =  readLines("/day23/input.txt")
    val elvePositions = parseElvePositions(input)

    val solution = solve(elvePositions)
    println("part1: ${solution.first}")
    println("part2: ${solution.second}")
}

fun solve(elvePositions: Set<Pair<Int, Int>>): Pair<Int, Int> {
    var finishRound = -1
    val rounds = 999999
    val currentElvePositions = HashSet(elvePositions)
    var elvePositionsAfterTenthRound = hashSetOf<Pair<Int, Int>>()
    for(i in 0 until rounds) {
        val listOfElvesByProposedPosition = hashMapOf<Pair<Int, Int>, ArrayList<Pair<Int, Int>>>()
        for (elvePosition in currentElvePositions) {
            if(shouldPropose(elvePosition, currentElvePositions)) {
                val proposedDirection = findFirstValidProposedDirectionOrNull(elvePosition, currentElvePositions, directionOrders[i % 4])
                if(proposedDirection == null) {
                    continue
                }
                val proposedPosition = proposedDirection.move(elvePosition)
                val listOfElvesForProposedPosition = listOfElvesByProposedPosition.getOrDefault(proposedPosition, arrayListOf())
                listOfElvesForProposedPosition.add(elvePosition)
                listOfElvesByProposedPosition[proposedPosition] = listOfElvesForProposedPosition
            }
        }
        if (noElfMoved(listOfElvesByProposedPosition)) {
            finishRound = i + 1
            break
        }
        for (entry in listOfElvesByProposedPosition.entries) {
            if(entry.value.size > 1) {
                continue
            }
            currentElvePositions.add(entry.key)
            currentElvePositions.remove(entry.value[0])
        }

        if(i == 9) {
            elvePositionsAfterTenthRound = HashSet(currentElvePositions)
        }
    }

    val minX = elvePositionsAfterTenthRound.minOf { it.first }
    val maxX = elvePositionsAfterTenthRound.maxOf { it.first }
    val minY = elvePositionsAfterTenthRound.minOf { it.second }
    val maxY = elvePositionsAfterTenthRound.maxOf { it.second }

    val deltaX = abs(minX - maxX) + 1
    val deltaY = abs(minY - maxY) + 1
    return Pair((deltaX * deltaY) - elvePositionsAfterTenthRound.size, finishRound)
}

fun noElfMoved(listOfElvesByProposedPosition: HashMap<Pair<Int, Int>, java.util.ArrayList<Pair<Int, Int>>>): Boolean {
    return listOfElvesByProposedPosition.isEmpty() || listOfElvesByProposedPosition.all { it.value.size > 1 }
}

fun findFirstValidProposedDirectionOrNull(
    elvePosition: Pair<Int, Int>,
    currentElvePositions: HashSet<Pair<Int, Int>>,
    directionOrder: Array<Direction>
): Direction? {
    return directionOrder.firstOrNull {directionToPropose -> adjFor.getValue(directionToPropose).none { it.move(elvePosition) in currentElvePositions } }
}

fun shouldPropose(elvePosition: Pair<Int, Int>, currentElvePositions: HashSet<Pair<Int, Int>>): Boolean {
    return Direction.values().any { it.move(elvePosition) in currentElvePositions }
}

fun parseElvePositions(input: List<String>): Set<Pair<Int, Int>> {
    val elvePositions = hashSetOf<Pair<Int, Int>>()
    for (i in input.indices) {
        for (j in input[i].indices) {
            if(input[i][j] == '#') {
                elvePositions.add(Pair(i, j))
            }
        }
    }

    return elvePositions
}

enum class Direction {
    NORTH {
        override fun move(position: Pair<Int, Int>): Pair<Int, Int> {
            return Pair(position.first - 1, position.second)
        }
    },
    SOUTH {
        override fun move(position: Pair<Int, Int>): Pair<Int, Int> {
            return Pair(position.first + 1, position.second)
        }
    },
    WEST {
        override fun move(position: Pair<Int, Int>): Pair<Int, Int> {
            return Pair(position.first, position.second - 1)
        }
    },
    EAST {
        override fun move(position: Pair<Int, Int>): Pair<Int, Int> {
            return Pair(position.first, position.second + 1)
        }
    },
    NORTH_EAST {
        override fun move(position: Pair<Int, Int>): Pair<Int, Int> {
            return Pair(position.first - 1, position.second + 1)
        }
    },
    NORTH_WEST {
        override fun move(position: Pair<Int, Int>): Pair<Int, Int> {
            return Pair(position.first - 1, position.second - 1)
        }
    },
    SOUTH_EAST {
        override fun move(position: Pair<Int, Int>): Pair<Int, Int> {
            return Pair(position.first + 1, position.second + 1)
        }
    },
    SOUTH_WEST {
        override fun move(position: Pair<Int, Int>): Pair<Int, Int> {
            return Pair(position.first + 1, position.second - 1)
        }
    };

    abstract fun move(position: Pair<Int, Int>): Pair<Int, Int>
}
