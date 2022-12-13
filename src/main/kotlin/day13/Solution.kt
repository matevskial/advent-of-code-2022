package day13

import readChunkedLines

fun main() {
    val packetPairs = readChunkedLines("/day13/input.txt")
        .map { Pair(parsePacket(it[0]), parsePacket(it[1])) }

    val numberOfPairsInRightOrder = packetPairs.withIndex().sumOf { if(comparePackets(it.value.first, it.value.second) < 0) it.index + 1 else 0 }
    println("part1: $numberOfPairsInRightOrder")

    val divider2 = listOf(listOf(listOf(2)))
    val divider6 = listOf(listOf(listOf(6)))
    val sortedPacketsWithDivider = packetPairs.flatMap { listOf(it.first, it.second) }
        .plusElement(divider2).plusElement(divider6)
        .sortedWith { o1, o2 -> comparePackets(o1, o2) }
    val decoderKey = (sortedPacketsWithDivider.indexOf(divider2) + 1) * (sortedPacketsWithDivider.indexOf(divider6) + 1)
    println("part2: $decoderKey")
}

fun parsePacket(packetInput: String): List<Any> {
    var currentIndex = 0
    fun parsePacketRec(packetInput: String, layer: Int): List<Any> {
        val elements = arrayListOf<Any>()
        while(currentIndex < packetInput.length) {
            if (packetInput[currentIndex] == '[') {
                currentIndex++
                elements.add(parsePacketRec(packetInput, layer + 1))
            } else if(packetInput[currentIndex].isDigit()) {
                var number = 0
                while(currentIndex < packetInput.length && packetInput[currentIndex].isDigit()) {
                    number = number * 10 + packetInput[currentIndex].digitToInt()
                    currentIndex++
                }
                elements.add(number)
            }
            else if (packetInput[currentIndex] == ',') {
                currentIndex++
            } else if(packetInput[currentIndex] == ']') {
                currentIndex++
                break
            }
        }
        return elements
    }

    return parsePacketRec(packetInput, 0)
}

fun comparePackets(packet1: List<*>, packet2: List<*>): Int {
    var i = 0
    var j = 0
    while(i in packet1.indices && j in packet2.indices) {
        val value = if (packet1[i] is Int && packet2[j] is Int) {
            (packet1[i] as Int).compareTo(packet2[j] as Int)
        } else if (packet1[i] is List<*> && packet2[j] is List<*>) {
            comparePackets(packet1[i] as List<*>, packet2[j] as List<*>)
        } else if(packet1[i] is Int) {
            comparePackets(listOf(packet1[i]), packet2[j] as List<*>)
        } else {
            comparePackets(packet1[i] as List<*>, listOf(packet2[j]))
        }

        if(value != 0) {
            return value
        }
        i++
        j++
    }

    return if(i !in packet1.indices && j !in packet2.indices) {
        0
    } else if(i !in packet1.indices) {
        -1
    } else {
        1
    }
}
