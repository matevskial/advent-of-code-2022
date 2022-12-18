package day16

import readLines

fun main() {
    val valves = parseValves(readLines("/day16/input.txt"))

    val distances = calculateDistanceBetweenEachValve(valves)
    val startValve = valves.last { it.name == "AA" }
    val valvesWithPressureRateHigherThanZero = valves.filter { it.rate > 0 }
    val maxReleasedPressure = calculateMaxReleasedPressureForUpperBoundMinutes(startValve, valvesWithPressureRateHigherThanZero, distances, 30)
    println("part1: $maxReleasedPressure")

    val part2 = part2(startValve, valvesWithPressureRateHigherThanZero, distances, 26)
    println("part2: $part2")
}

fun part2(
    startValve: Valve,
    valvesWithPressureRateHigherThanZero: List<Valve>,
    distances: Map<Valve, Map<Valve, Int>>,
    upperBoundInMinutes: Int
): Int {
    val combinations = generateCombinations(valvesWithPressureRateHigherThanZero, valvesWithPressureRateHigherThanZero.size / 2)

    var max = Int.MIN_VALUE
    for (combination in combinations) {
        val complementCombination = arrayListOf<Valve>()
        for (valve in valvesWithPressureRateHigherThanZero) {
            if (valve !in combination) {
                complementCombination.add(valve)
            }
        }
        val m1 = calculateMaxReleasedPressureForUpperBoundMinutes(startValve, combination, distances, upperBoundInMinutes)
        val m2 = calculateMaxReleasedPressureForUpperBoundMinutes(startValve, complementCombination, distances, upperBoundInMinutes)
        max = maxOf(max, m1 + m2)
    }

    return max
}

fun generateCombinations(valvesWithPressureRateHigherThanZero: List<Valve>, sampleSize: Int): List<List<Valve>> {
    val combinations = arrayListOf<List<Valve>>()

    val currentCombination = arrayOfNulls<Valve>(sampleSize).map { Valve("69", 69) }.toTypedArray()
    fun helper(currentCombination: Array<Valve>, start: Int, end: Int, index: Int) {
        if(index == currentCombination.size) {
            combinations.add(currentCombination.toList())
        } else if(start <= end) {
            currentCombination[index] = valvesWithPressureRateHigherThanZero[start]
            helper(currentCombination, start + 1, end, index + 1)
            helper(currentCombination, start + 1, end, index)
        }
    }

    helper(currentCombination, 0, valvesWithPressureRateHigherThanZero.size - 1, 0)
    return combinations
}

fun calculateMaxReleasedPressureForUpperBoundMinutes(startValve: Valve, valvesWithPressureRateHigherThanZero: List<Valve>, distances:  Map<Valve, Map<Valve, Int>>, upperBoundInMinutes: Int): Int {
    var max = Int.MIN_VALUE

    val chosenValves = hashSetOf<Valve>()
    var currentPressure = 0
    fun rec(prevChosenValve: Valve, minute: Int) {
        if(minute >= upperBoundInMinutes) {
            return
        }

        for (valveToChoose in valvesWithPressureRateHigherThanZero) {
            if (valveToChoose in chosenValves) {
                continue
            }
            if (valveToChoose !in distances.getValue(prevChosenValve)) {
                continue
            }

            chosenValves.add(valveToChoose)
            val nextMinute =  minute + distances.getValue(prevChosenValve).getValue(valveToChoose) + 1
            val pressure = valveToChoose.rate * (upperBoundInMinutes - nextMinute)
            currentPressure += pressure
            max = maxOf(max, currentPressure)
            rec(valveToChoose, nextMinute)
            chosenValves.remove(valveToChoose)
            currentPressure -= pressure
        }
    }

    rec(startValve, 0)
    return max
}

fun calculateDistanceBetweenEachValve(valves: List<Valve>): Map<Valve, Map<Valve, Int>> {
    val distances = hashMapOf<Valve, MutableMap<Valve, Int>>()

    fun bfs(startValve: Valve) {
        val distancesFromStartValve = hashMapOf<Valve, Int>()
        val queue = ArrayDeque<Valve>()
        queue.addFirst(startValve)
        distancesFromStartValve[startValve] = 0

        while(queue.isNotEmpty()) {
            val currentValve = queue.removeLast()
            val currentDistance = distancesFromStartValve.getValue(currentValve)

            for(nextValve in currentValve.getValves()) {
                if (nextValve !in distancesFromStartValve) {
                    queue.addFirst(nextValve)
                    distancesFromStartValve[nextValve] = currentDistance + 1
                }
            }
        }

        distances[startValve] = distancesFromStartValve
    }

    for(valve in valves) {
        bfs(valve)
    }

    return distances
}

fun parseValves(input: List<String>): List<Valve> {
    val valves = input.map { valveInput -> parseValve(valveInput) }
    val valvesByName = valves.associateBy { it.name }

    for ((index, line) in input.withIndex()) {
        val valveNameRegex = "[A-Z]{2}".toRegex()
        val valveNames = valveNameRegex.findAll(line).map { it.value }.drop(1).toList()
        for (valveName in valveNames) {
            valves[index].addValve(valvesByName.getValue(valveName))
        }
    }

    return valves
}

fun parseValve(valveInput: String): Valve {
    val valveNameRegex = "[A-Z]{2}".toRegex()
    val valveRateRegex = "[+-]*\\d+".toRegex()
    return Valve(
        valveNameRegex.findAll(valveInput).map { it.value }.first(),
        valveRateRegex.findAll(valveInput).map { it.value }.first().toInt()
    )
}

class Valve(val name: String, val rate: Int) {

    private val valves = arrayListOf<Valve>()

    fun addValve(valve: Valve) {
        valves.add(valve)
    }

    fun getValves(): List<Valve> {
        return valves.toList()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Valve

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
