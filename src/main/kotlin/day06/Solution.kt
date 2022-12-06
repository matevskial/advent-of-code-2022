package day06

import readLines
import java.lang.Integer.min

fun main() {
    val buffer = readLines("/day06/input.txt")[0]
    val indexOfStartOfPacketMarker = calculateNumberOfCharactersIncludingFirstStartOfMarker(buffer, 4)
    println("part1: $indexOfStartOfPacketMarker")

    val indexOfStartOfMessageMarker = calculateNumberOfCharactersIncludingFirstStartOfMarker(buffer, 14)
    println("part2: $indexOfStartOfMessageMarker")
}

fun calculateNumberOfCharactersIncludingFirstStartOfMarker(buffer: String, markerLength: Int): Int {
    for(i in buffer.indices) {
        if(buffer.substring(i, min(buffer.length, i + markerLength)).toSet().size == markerLength) {
            return i + markerLength
        }
    }
    return -1
}
