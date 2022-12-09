package day09

import readLines

enum class Dir(val dx: Int, val dy: Int) {
    D(0, 1),
    U(0, -1),
    L(-1, 0),
    R(1, 0)
}

fun main() {
    val motions = readLines("/day09/input.txt")
    var visitedPositions = calculateNumberOfVisitedPositionsByTail(motions, List(2) { Knot(Pair(0, 0), Pair(0, 0)) })
    println("part1: $visitedPositions")

    visitedPositions = calculateNumberOfVisitedPositionsByTail(motions, List(10) { Knot(Pair(0, 0), Pair(0, 0)) })
    println("part2: $visitedPositions")
}

fun calculateNumberOfVisitedPositionsByTail(motions: List<String>, rope: List<Knot>): Int {
    val visitedPositionsByTail = hashSetOf(Pair(0, 0))
    for (motion in motions) {
        val dir = Dir.valueOf(motion.split(" ")[0])
        val steps = motion.split(" ")[1].toInt()
        for (step in 0 until steps) {
            moveHead(rope, dir)
            followHead(rope)
            visitedPositionsByTail.add(rope.last().currentPosition)
        }
    }
    return visitedPositionsByTail.size
}

fun followHead(rope: List<Knot>) {
    for (index in 1 until rope.size) {
        val tail = rope[index]
        val head = rope[index - 1]
        moveKnot(tail, head)
    }
}

fun moveKnot(tail: Knot, head: Knot) {
    if(shouldMoveDown(tail, head)) {
        moveKnot(tail, Dir.D)
        if(head.currentPosition.first < tail.currentPosition.first) {
            moveKnot(tail, Dir.L)
        } else if(head.currentPosition.first > tail.currentPosition.first) {
            moveKnot(tail, Dir.R)
        }
    } else if(shouldMoveUp(tail, head)) {
        moveKnot(tail, Dir.U)
        if(head.currentPosition.first < tail.currentPosition.first) {
            moveKnot(tail, Dir.L)
        } else if(head.currentPosition.first > tail.currentPosition.first) {
            moveKnot(tail, Dir.R)
        }
    } else if(shouldMoveLeft(tail, head)) {
        moveKnot(tail, Dir.L)
        if(head.currentPosition.second < tail.currentPosition.second) {
            moveKnot(tail, Dir.U)
        } else if(head.currentPosition.second > tail.currentPosition.second) {
            moveKnot(tail, Dir.D)
        }
    } else if(shouldMoveRight(tail, head)) {
        moveKnot(tail, Dir.R)
        if(head.currentPosition.second < tail.currentPosition.second) {
            moveKnot(tail, Dir.U)
        } else if(head.currentPosition.second > tail.currentPosition.second) {
            moveKnot(tail, Dir.D)
        }
    }
}

fun shouldMoveRight(tail: Knot, head: Knot): Boolean {
    return head.currentPosition.first - tail.currentPosition.first > 1
}

fun shouldMoveLeft(tail: Knot, head: Knot): Boolean {
    return tail.currentPosition.first - head.currentPosition.first > 1
}

fun shouldMoveUp(tail: Knot, head: Knot): Boolean {
    return tail.currentPosition.second - head.currentPosition.second > 1
}

fun shouldMoveDown(tail: Knot, head: Knot): Boolean {
    return head.currentPosition.second - tail.currentPosition.second > 1
}

fun moveHead(rope: List<Knot>, dir: Dir) {
    moveKnot(rope.first(), dir)
}

fun moveKnot(knot: Knot, dir: Dir) {
    knot.previousPosition = knot.currentPosition
    knot.currentPosition = Pair(knot.currentPosition.first + dir.dx, knot.currentPosition.second + dir.dy)
}

data class Knot(var currentPosition: Pair<Int, Int>, var previousPosition: Pair<Int, Int>)
