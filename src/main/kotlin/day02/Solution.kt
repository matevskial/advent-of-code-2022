package day02

import readLines

fun main() {
    val scoreByMove = mapOf('A' to 1, 'B' to 2, 'C' to 3)
    val moves = mapOf('X' to 'A', 'Y' to 'B', 'Z' to 'C', 'A' to 'A', 'B' to 'B', 'C' to 'C')
    val beats = mapOf('A' to 'C', 'C' to 'B', 'B' to 'A')
    val loses = mapOf('A' to 'B', 'B' to 'C', 'C' to 'A')
    val rounds = readLines("/day02/input.txt").map { round -> round.split(" ").map { it.first() } }
    var score = 0
    for (round in rounds) {
        score += scoreByMove.getValue(moves.getValue(round.last()))
        if(beats.getValue(moves.getValue(round.last())) == round.first()) {
            score += 6
        } else if(round.first() == moves.getValue(round.last())) {
            score += 3
        }
    }
    println("part1: $score")

    score = 0
    for(round in rounds) {
        if(round.last() == 'X') {
            score += scoreByMove.getValue(beats.getValue(round.first()))
        } else if(round.last() == 'Y') {
            score += 3 + scoreByMove.getValue(round.first())
        } else if(round.last() == 'Z') {
            score += 6 + scoreByMove.getValue(loses.getValue(round.first()))
        }
    }

    println("part 2: $score")
}
