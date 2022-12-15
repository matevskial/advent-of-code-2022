package day15

import readLines
import kotlin.math.abs
import kotlin.math.max

fun main() {
    val sensors = readLines("/day15/input.txt")
        .map { parseSensor(it) }
    val numberOfPositionsThatCannotContainBeacon =  findPositionsWhereLocationForBeaconIsNotPossibleForY(2000000, sensors)
    println("part1: $numberOfPositionsThatCannotContainBeacon")

    val distressBeacon = findDistressBeacon(sensors)
    println("part2: ${distressBeacon.first * 4000000L + distressBeacon.second}")
}

fun findDistressBeacon(sensors: List<Sensor>): Pair<Int, Int> {
    val xRange = 0..4000000
    val yRange = 0..4000000

    for(y in yRange) {
        val xIntervalsSortedAndMerged = getXIntervalsForY(sensors, y)
        if(xIntervalsSortedAndMerged.isEmpty()) {
            return Pair(y, 0)
        }
        for ((index, interval) in xIntervalsSortedAndMerged.withIndex()) {
            if(index == 0 || interval.first - 1 > xIntervalsSortedAndMerged[index - 1].second) {
                if(interval.first - 1 in xRange) {
                    return Pair(interval.first - 1, y)
                }
            } else if(index == xIntervalsSortedAndMerged.size - 1 || interval.second + 1 < xIntervalsSortedAndMerged[index + 1].first) {
                if(interval.second + 1 in xRange) {
                    return Pair(interval.second + 1, y)
                }
            }
        }
    }

    return Pair(0, 0)
}

fun getXIntervalsForY(sensors: List<Sensor>, y: Int): List<Pair<Int, Int>> {
    val xIntervals = arrayListOf<Pair<Int, Int>>()
    for (sensor in sensors) {
        val yDist = abs(y - sensor.position.second)
        if (yDist > sensor.getDistanceToBeacon()) {
            continue
        }
        val n = 2 * abs(yDist - sensor.getDistanceToBeacon()) + 1
        xIntervals.add(Pair(sensor.position.first - (n / 2), sensor.position.first + (n / 2)))
    }
    return sortAndMergeIntervals(xIntervals)
}

fun sortAndMergeIntervals(intervals: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
    val sortedIntervals = intervals.sortedWith {o1, o2 -> if(o1.first.compareTo(o2.first) == 0) o1.second.compareTo(o2.second) else o1.first.compareTo(o2.first)}
    val sortedAndMergedIntervals = arrayListOf<Pair<Int, Int>>()
    for (interval in sortedIntervals) {
        if (sortedAndMergedIntervals.size == 0 || interval.first > sortedAndMergedIntervals.last().second) {
            sortedAndMergedIntervals.add(interval)
        } else {
            sortedAndMergedIntervals[sortedAndMergedIntervals.lastIndex] =
                Pair(sortedAndMergedIntervals.last().first, max(sortedAndMergedIntervals.last().second, interval.second))
        }
    }
    return sortedAndMergedIntervals
}

fun findPositionsWhereLocationForBeaconIsNotPossibleForY(y: Int, sensors: List<Sensor>): Int {

    val xIntervals = getXIntervalsForY(sensors, y)
    var count = 0
    for(interval in xIntervals) {
        count += (interval.second - interval.first + 1) - countSensorsAndBeaconsInInterval(sensors, interval, y)
    }

    return count
}

fun countSensorsAndBeaconsInInterval(sensors: List<Sensor>, xInterval: Pair<Int, Int>, y: Int): Int {
    return sensors.count {
        (it.closestBeaconPosition.first in xInterval.first..xInterval.second && it.closestBeaconPosition.second == y)
                || (it.position.first in xInterval.first..xInterval.second && it.position.second == y)}
}

fun parseSensor(sensorInput: String): Sensor {
    val numberMatcher = "[+-]*\\d+".toRegex()
    val numbers = numberMatcher.findAll(sensorInput).map { it.value.toInt() }.toList()
    return Sensor(Pair(numbers[0], numbers[1]), Pair(numbers[2], numbers[3]))
}

data class Sensor(val position: Pair<Int, Int>, val closestBeaconPosition: Pair<Int, Int>) {
    fun getDistanceToBeacon(): Int {
        return abs(position.first - closestBeaconPosition.first) + abs(position.second - closestBeaconPosition.second)
    }
}
