package day10

import readLines

fun main() {
    val program = readLines("day10/input.txt")

    val (sumOfSignalStrengths, crt) = executeProgram(program, 6)
    println("part1: $sumOfSignalStrengths")

    println("part2:")
    crt.chunked(40).forEach { println(it.joinToString("")) }
}

fun executeProgram(program: List<String>, numberOfSignalStrengths: Int): Pair<Long, List<Char>> {
    val signalStrengths = arrayListOf<Long>()
    var currentExpectedCycle = 20
    var x = 1L
    var currentCycle = 0
    val crt = arrayOfNulls<Char>(240).map { '.' }.toMutableList()
    for(instruction in program) {
        val opCode = instruction.split(" ")[0]
        var cycles = 0
        if(opCode == "addx") {
            cycles = 2
        } else if(opCode == "noop") {
            cycles = 1
        }

        for (c in 1..cycles) {
            currentCycle++
            if(currentCycle == currentExpectedCycle && signalStrengths.size < numberOfSignalStrengths) {
                signalStrengths.add(x * currentExpectedCycle)
                currentExpectedCycle += 40
            }

            val positionOfPixelInRow = (currentCycle - 1) % 40
            if(positionOfPixelInRow in x-1..x+1) {
                crt[currentCycle - 1] = '#'
            } else {
                crt[currentCycle - 1] = '.'
            }
        }

        if(opCode == "addx") {
            x += instruction.split(" ")[1].toLong()
        }
    }
    return Pair(signalStrengths.sum(), crt)
}
