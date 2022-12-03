package day01

import readChunkedLines

fun main() {
    val caloriesPerElf = readChunkedLines("/day01/input.txt").map { lines -> lines.map { it.toInt() } }
    val sortedTotalCaloriesPerElf = caloriesPerElf.map { it.sum() }.sortedDescending()
    val maxTotalCalories = sortedTotalCaloriesPerElf.first()
    println("part1 $maxTotalCalories")

    val sumOfTopThreeTotalCalories = sortedTotalCaloriesPerElf.chunked(3).first().sum()
    println("part2 $sumOfTopThreeTotalCalories")
}
