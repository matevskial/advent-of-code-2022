package day16

import readLines

fun main() {
    val valves = parseValves(readLines("/day16/input.txt"))
    val maxReleasedPressure = calculateMaxReleasedPressureForUpperBoundMinutes(valves, 30)
    println("part1: $maxReleasedPressure")
}


fun calculateMaxReleasedPressureForUpperBoundMinutes(valves: List<Valve>, upperBoundInMinutes: Int): Int {
    val distances = calculateDistanceBetweenEachValve(valves)
    val startValve = valves.last { it.name == "AA" }
    val valvesWithPressureRateHigherThanZero = valves.filter { it.rate > 0 }
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

    val valves = input.map { parseValve(it) }
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
