package day19

import readLines
import kotlin.math.max

private const val PART1_MINUTES = 24
private const val PART2_MINUTES = 32

fun main() {
    val blueprints = readLines("/day19/input.txt").map { parseBlueprint(it) }
    val sumOfQualityLevels = blueprints.sumOf { calculateQualityLevel(it) }
    println("part1: $sumOfQualityLevels")

    val sumOfLargestNumberOfGeodes = blueprints.take(3).sumOf { calculateLargestNumberOfGeodes(it, PART2_MINUTES) }
    println("part2: $sumOfLargestNumberOfGeodes")
//    println(calculateLargestNumberOfGeodes(blueprints.last()))
}

fun calculateQualityLevel(blueprint: Blueprint): Long {
    return blueprint.id * calculateLargestNumberOfGeodes(blueprint, PART1_MINUTES)
}

fun calculateLargestNumberOfGeodes(blueprint: Blueprint, upperBoundMinutes: Int): Long {
    fun calculateMinuteAmountToCollectRemainingMaterialOfType(material: Long, robots: Long): Long {
        return if (robots == 0L) Long.MIN_VALUE else {
            (material / robots) + if (material % robots != 0L) 1L else 0L
        }
    }

    fun rec(robotResourcesAtBeginOfThisMinute: Resources, materialResourcesAtBeginOfThisMinute: Resources, minute: Long): Long {
        if(minute >= upperBoundMinutes) {
            val minutes = upperBoundMinutes - minute + 1
            val materialResourcesAtEndOfFinalMinute = materialResourcesAtBeginOfThisMinute.plus(
                robotResourcesAtBeginOfThisMinute.times(minutes)
            )
            return materialResourcesAtEndOfFinalMinute.get(ResourceType.GEODE)
        }

        var result = Long.MIN_VALUE
        for (robotTypeToBuy in ResourceType.values()) {
            if (blueprint.canCollectResourcesForBuyingRobotOfType(robotTypeToBuy, robotResourcesAtBeginOfThisMinute)) {
                val remainingMaterialResourcesToCollect = blueprint.getRobotCost(robotTypeToBuy).minusWithMaxZero(materialResourcesAtBeginOfThisMinute)
                val minuteAmountToCollectRemainingMaterialResources = ResourceType.values().maxOf { resourceType ->
                    calculateMinuteAmountToCollectRemainingMaterialOfType(remainingMaterialResourcesToCollect.get(resourceType), robotResourcesAtBeginOfThisMinute.get(resourceType))
                }
                val nextMinute = minute + minuteAmountToCollectRemainingMaterialResources + 1
                val materialResourcesAtBeginOfNextMinute = materialResourcesAtBeginOfThisMinute
                    .plus(robotResourcesAtBeginOfThisMinute.times(minuteAmountToCollectRemainingMaterialResources + 1))
                    .minus(blueprint.getRobotCost(robotTypeToBuy))
                val robotResourcesAtBeginOfNextMinute = robotResourcesAtBeginOfThisMinute.plus(robotTypeToBuy, 1)
                result = maxOf(result, rec(robotResourcesAtBeginOfNextMinute, materialResourcesAtBeginOfNextMinute, nextMinute))
            }
        }

        val materialResourcesAtBeginOfFinalMinute = materialResourcesAtBeginOfThisMinute.plus(
            robotResourcesAtBeginOfThisMinute.times(upperBoundMinutes - minute)
        )
        result = maxOf(result, rec(robotResourcesAtBeginOfThisMinute, materialResourcesAtBeginOfFinalMinute, upperBoundMinutes.toLong()))
        return result
    }

    return rec(Resources(1, 0, 0, 0), Resources(0, 0, 0, 0), 1)
}

class Resources(ore: Long, clay: Long, obsidian: Long, geode: Long) {
    private val resourcesArr = Array(4) { 0L }

    init {
        resourcesArr[ResourceType.ORE.ordinal] = ore
        resourcesArr[ResourceType.CLAY.ordinal] = clay
        resourcesArr[ResourceType.OBSIDIAN.ordinal] = obsidian
        resourcesArr[ResourceType.GEODE.ordinal] = geode
    }

    fun get(resourceType: ResourceType): Long {
        return resourcesArr[resourceType.ordinal]
    }

    fun plus(resources: Resources): Resources {
        return Resources(
            resourcesArr[ResourceType.ORE.ordinal] + resources.get(ResourceType.ORE),
            resourcesArr[ResourceType.CLAY.ordinal] + resources.get(ResourceType.CLAY),
            resourcesArr[ResourceType.OBSIDIAN.ordinal] + resources.get(ResourceType.OBSIDIAN),
            resourcesArr[ResourceType.GEODE.ordinal] + resources.get(ResourceType.GEODE)
        )
    }

    fun plus(resourceType: ResourceType, n: Long): Resources {
        val newResources = Resources(resourcesArr[0], resourcesArr[1], resourcesArr[2], resourcesArr[3])
        newResources.resourcesArr[resourceType.ordinal] += n
        return newResources
    }

    fun minus(resources: Resources): Resources {
        return Resources(
            resourcesArr[ResourceType.ORE.ordinal] - resources.get(ResourceType.ORE),
            resourcesArr[ResourceType.CLAY.ordinal] - resources.get(ResourceType.CLAY),
            resourcesArr[ResourceType.OBSIDIAN.ordinal] - resources.get(ResourceType.OBSIDIAN),
            resourcesArr[ResourceType.GEODE.ordinal] - resources.get(ResourceType.GEODE)
        )
    }

    fun minusWithMaxZero(resources: Resources): Resources {
        return Resources(
            max(0L, resourcesArr[ResourceType.ORE.ordinal] - resources.get(ResourceType.ORE)),
            max(0L, resourcesArr[ResourceType.CLAY.ordinal] - resources.get(ResourceType.CLAY)),
            max(0L, resourcesArr[ResourceType.OBSIDIAN.ordinal] - resources.get(ResourceType.OBSIDIAN)),
            max(0L, resourcesArr[ResourceType.GEODE.ordinal] - resources.get(ResourceType.GEODE))
        )
    }

    fun times(n: Long): Resources {
        return Resources(
            resourcesArr[0] * n,
            resourcesArr[1] * n,
            resourcesArr[2] * n,
            resourcesArr[3] * n
        )
    }
}

enum class ResourceType {
    ORE, CLAY, OBSIDIAN, GEODE
}

fun parseBlueprint(blueprintInput: String): Blueprint {
    val numbersRegex = "[+-]*\\d+".toRegex()
    val numbers = numbersRegex.findAll(blueprintInput).map { it.value }.toList()

    val oreRobotMaterialResources = Resources(numbers[1].toLong(), 0L, 0L, 0)
    val clayRobotMaterialResources = Resources(numbers[2].toLong(), 0L, 0L, 0)
    val obsidianRobotMaterialResources = Resources(numbers[3].toLong(), numbers[4].toLong(), 0, 0)
    val geodeRobotMaterialResources = Resources(numbers[5].toLong(), 0L, numbers[6].toLong(), 0)

    return Blueprint(numbers[0].toInt(), oreRobotMaterialResources, clayRobotMaterialResources, obsidianRobotMaterialResources, geodeRobotMaterialResources)
}

class Blueprint(
    val id: Int,
    oreRobotMaterialResources: Resources,
    clayRobotMaterialResources: Resources,
    obsidianRobotMaterialResources: Resources,
    geodeRobotMaterialResources: Resources
) {
    private val robotCost: Map<ResourceType, Resources>

    init {
        robotCost = mapOf(
            ResourceType.ORE to oreRobotMaterialResources,
            ResourceType.CLAY to clayRobotMaterialResources,
            ResourceType.OBSIDIAN to obsidianRobotMaterialResources,
            ResourceType.GEODE to geodeRobotMaterialResources
        )
    }

    fun getRobotCost(resourceType: ResourceType): Resources {
        return robotCost.getValue(resourceType)
    }

    fun canCollectResourcesForBuyingRobotOfType(robotType: ResourceType, robotResources: Resources): Boolean {
        for(resourceTypeToSpend in ResourceType.values()) {
            val canSpendResource =
                if (robotCost.getValue(robotType).get(resourceTypeToSpend) == 0L) true else robotResources.get(
                    resourceTypeToSpend
                ) > 0
            if (!canSpendResource) {
                return false
            }
        }
        return true
    }
}
