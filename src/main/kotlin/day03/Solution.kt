package day03

import readLines

fun main() {
    val rucksacks = readLines("/day03/input.txt")
    val itemTypes = "abcdefghijklmnopqrstuvwxyz" + "abcdefghijklmnopqrstuvwxyz".uppercase()
    var sumOfPriorities = 0
    for(r in rucksacks) {
        val firstCompartment = r.substring(0, r.length / 2)
        val secondCompartment = r.substring(r.length / 2)
        val intersection = firstCompartment.toSet().intersect(secondCompartment.toSet()).first()
        sumOfPriorities += itemTypes.indexOf(intersection) + 1
    }

    println("part1: $sumOfPriorities")

    sumOfPriorities = 0
    for(i in rucksacks.indices step 3) {
        val intersection = rucksacks[i].toSet().intersect(rucksacks[i + 1].toSet()).intersect(rucksacks[i + 2].toSet()).first()
        sumOfPriorities += itemTypes.indexOf(intersection) + 1
    }

    println("part2: $sumOfPriorities")
}
