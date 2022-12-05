package day04

import readLines

fun main() {
    val rangePairs = readLines("/day04/input.txt")
        .flatMap { it.split(",") }
        .map { it.split("-") }
        .map {Pair(it[0].toLong(), it[1].toLong())}
        .chunked(2)

    val numberOfPairsWithOneRangeFullyContainingOtherRange = rangePairs
        .map { isPairWithOneRangeFullyContainingOtherRange(it) }
        .sumOf { if (it) 1L else 0L }
    println("part1: $numberOfPairsWithOneRangeFullyContainingOtherRange")

    val numberOfPairsWithOneRangeOverlappingOtherRange = rangePairs
        .map { isPairWithOneRangeOverlappingOtherRange(it) }
        .sumOf { if (it) 1L else 0L }
    println("part2: $numberOfPairsWithOneRangeOverlappingOtherRange")
}

fun isPairWithOneRangeOverlappingOtherRange(rangePair: List<Pair<Long, Long>>): Boolean {
    return isFirstRangeOverlappingSecondRange(rangePair[0], rangePair[1])
}

fun isFirstRangeOverlappingSecondRange(firstRange: Pair<Long, Long>, secondRange: Pair<Long, Long>): Boolean {
    return !(firstRange.first > secondRange.second || firstRange.second < secondRange.first)
}

fun isPairWithOneRangeFullyContainingOtherRange(rangePair: List<Pair<Long, Long>>): Boolean {
    return isFirstRangeFullyContainingSecondRange(rangePair[0], rangePair[1])
            || isFirstRangeFullyContainingSecondRange(rangePair[1], rangePair[0])
}

fun isFirstRangeFullyContainingSecondRange(firstRange: Pair<Long, Long>, secondRange: Pair<Long, Long>): Boolean {
    return firstRange.first <= secondRange.first && firstRange.second >= secondRange.second
}
