package day12

import day09.Dir
import readLines

fun main() {
    val heightMap = readLines("/day12/input.txt").map { it.toList() }
    val allStartPositions = getAllPositions(heightMap, 'S')
    val endPosition = getAllPositions(heightMap, 'E')[0]
    var fewestSteps = allStartPositions.filter { heightMap[it.first][it.second] == 'S' }
        .minOfOrNull { calculateFewestStepsWithBfs(heightMap, it, endPosition) }
    println("part1: $fewestSteps")

    fewestSteps = allStartPositions.minOfOrNull { calculateFewestStepsWithBfs(heightMap, it, endPosition) }
    println("part2: $fewestSteps")
}

fun getAllPositions(heightMap: List<List<Char>>, positionCharacter: Char): List<Pair<Int, Int>> {
    val allPositions = arrayListOf<Pair<Int, Int>>()
    for ((i, row) in heightMap.withIndex()) {
        for((j, char) in row.withIndex()) {
            if(char == positionCharacter) {
                allPositions.add(Pair(i, j))
            } else if(char == 'a' && positionCharacter == 'S') {
                allPositions.add(Pair(i, j))
            }
        }
    }
    return allPositions
}

fun calculateFewestStepsWithBfs(heightMap: List<List<Char>>, startPosition: Pair<Int, Int>, endPosition: Pair<Int, Int>): Int {
    val distances = hashMapOf<Pair<Int, Int>, Int>()
    val queue = ArrayDeque<Pair<Int, Int>>()
    distances[startPosition] = 0
    queue.addFirst(startPosition)

    while(queue.isNotEmpty()) {
        val currentPosition = queue.removeLast()
        val distanceToCurrentPosition = distances.getValue(currentPosition)
        val validPositionsToVisit = getValidPositionsToVisit(currentPosition, heightMap)
        for (newPosition in validPositionsToVisit) {
            if(newPosition in distances) {
                continue
            }
            distances[newPosition] = distanceToCurrentPosition + 1
            queue.addFirst(newPosition)
        }
    }

    return distances.getOrDefault(endPosition, Int.MAX_VALUE)
}

fun getValidPositionsToVisit(currentPosition: Pair<Int, Int>, heightMap: List<List<Char>>): List<Pair<Int, Int>> {
    val validPositionsToVisit = arrayListOf<Pair<Int, Int>>()
    for (d in Dir.values()) {
        val newPosition = Pair(currentPosition.first + d.dx, currentPosition.second + d.dy)
        if(isPositionInBound(newPosition, heightMap) &&
            (getPositionCharacter(currentPosition, heightMap) >= getPositionCharacter(newPosition, heightMap)
                    || getPositionCharacter(currentPosition, heightMap) + 1 == getPositionCharacter(newPosition, heightMap))
        ) {
            validPositionsToVisit.add(newPosition)
        }
    }
    return validPositionsToVisit
}

fun getPositionCharacter(position: Pair<Int, Int>, heightMap: List<List<Char>>): Char {
    return when(heightMap[position.first][position.second]) {
        'S' -> 'a'
        'E' -> 'z'
        else -> heightMap[position.first][position.second]
    }
}

fun isPositionInBound(position: Pair<Int, Int>, heightMap: List<List<Char>>): Boolean {
    return position.first >= 0 && position.first < heightMap.size
            && position.second >= 0 && position.second < heightMap[0].size
}
