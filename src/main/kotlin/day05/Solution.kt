package day05

import readChunkedLines
import kotlin.collections.ArrayDeque
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

fun main() {
    val input = readChunkedLines("/day05/input.txt")
    val rearrangementProcedure = parseMovesInput(input[1])

    var resultStacks = executeRearrangementProcedure(parseStacksInput(input[0]), rearrangementProcedure)
    println("part1: ${combineTopOfStacks(resultStacks)}")

    resultStacks = executeRearrangementProcedureForPart2(parseStacksInput(input[0]), rearrangementProcedure)
    println("part2: ${combineTopOfStacks(resultStacks)}")
}

fun executeRearrangementProcedureForPart2(
    stacks: List<ArrayDeque<Char>>,
    rearrangementProcedure: List<Triple<Int, Int, Int>>
): List<ArrayDeque<Char>> {
    for(rearrangement in rearrangementProcedure) {
        val itemsToMove = arrayListOf<Char>()
        for (count in 0 until rearrangement.first) {
            itemsToMove.add(stacks[rearrangement.second].removeLast())
        }
        stacks[rearrangement.third].addAll(itemsToMove.reversed())
    }
    return stacks
}

fun combineTopOfStacks(stacks: List<ArrayDeque<Char>>): String {
    return stacks.mapNotNull { it.lastOrNull() }.joinToString("")
}

fun executeRearrangementProcedure(stacks: List<ArrayDeque<Char>>, rearrangementProcedure: List<Triple<Int, Int, Int>>): List<ArrayDeque<Char>> {
    for(rearrangement in rearrangementProcedure) {
        for (count in 0 until rearrangement.first) {
            stacks[rearrangement.third].addLast(stacks[rearrangement.second].removeLast())
        }
    }
    return stacks
}

fun parseMovesInput(input: List<String>): List<Triple<Int, Int, Int>> {
    return input
        .map { it.split(" ") }
        .map { it.map { token -> token.toIntOrNull() } }
        .map { it.filterNotNull() }
        .map { Triple(it[0], it[1] - 1, it[2] - 1) }
}

fun parseStacksInput(input: List<String>): List<ArrayDeque<Char>> {
    val reversedStacks = HashMap<Int, ArrayList<Char>>()
    for (line in input) {
        for((i, character) in line.withIndex()) {
            if(character.isUpperCase()) {
                val stackNumber = calculateStackNumber(i)
                val stack = reversedStacks.getOrDefault(stackNumber, arrayListOf())
                stack.add(character)
                reversedStacks[stackNumber] = stack
            }
        }
    }

    return reversedStacks.toSortedMap().map { ArrayDeque(it.value.reversed())}
}

fun calculateStackNumber(characterPositionInLine: Int): Int {
    return (characterPositionInLine - 1) / 4
}
