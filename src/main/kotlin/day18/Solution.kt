package day18

import readLines

fun main() {
    val cubes = readLines("/day18/input.txt")
        .map { it.split(",") }
        .map { Triple(it[0].toInt(), it[1].toInt(), it[2].toInt()) }

    val surfaceAreaCubes = calculateSurfaceArea(cubes)
    println("part1: ${surfaceAreaCubes.size}")

    val exteriorSurfaceArea = calculateExteriorSurfaceArea(surfaceAreaCubes, cubes.toSet())
    println("part2: $exteriorSurfaceArea")
}

fun calculateExteriorSurfaceArea(surfaceAreaCubes: List<Triple<Int, Int, Int>>, cubes: Set<Triple<Int, Int, Int>>): Int {
    val surfaceAreaCubesSet = surfaceAreaCubes.toSet()
    val exteriorSurfaceAreaCubesSet = hashSetOf<Triple<Int, Int, Int>>()
    val trappedSurfaceAreaCubesSet = hashSetOf<Triple<Int, Int, Int>>()
    val visitedCubes = hashSetOf<Triple<Int, Int, Int>>()
    val ranges = calculateCubeBounds(cubes)
    fun isCubeOutOfBounds(cube: Triple<Int, Int, Int>, ranges: Triple<IntRange, IntRange, IntRange>): Boolean {
        return cube.first !in ranges.first || cube.second !in ranges.second || cube.third !in ranges.third
    }

    fun bfs(startCube: Triple<Int, Int, Int>) {
        var isTrapped = true
        val queue = ArrayDeque<Triple<Int, Int, Int>>()
        val visitedCubesByCurrentBfs = hashSetOf(startCube)
        queue.addFirst(startCube)
        visitedCubes.add(startCube)

        while (queue.isNotEmpty()) {
            val current = queue.removeLast()

            for(adj in getAdjacentCubes(current)) {
                if(adj in visitedCubes) {
                    continue
                }
                if (adj in cubes) {
                    continue
                }
                if(isCubeOutOfBounds(adj, ranges)) {
                    isTrapped = false
                    continue
                }
                queue.add(adj)
                visitedCubes.add(adj)
                visitedCubesByCurrentBfs.add(adj)
            }
        }
        val surfaceAreCubesVisited = visitedCubesByCurrentBfs.filter { it in surfaceAreaCubesSet }
        if(!isTrapped) {
            exteriorSurfaceAreaCubesSet.addAll(surfaceAreCubesVisited)
        } else {
            trappedSurfaceAreaCubesSet.addAll(surfaceAreCubesVisited)
        }
    }

    for (cube in surfaceAreaCubesSet) {
        if (cube !in visitedCubes) {
            bfs(cube)
        }
    }
    var exteriorSurfaceArea = 0
    for(cube in exteriorSurfaceAreaCubesSet) {
        exteriorSurfaceArea += getAdjacentCubes(cube).filter { it in cubes }.toSet().size
    }
    return exteriorSurfaceArea
}

fun calculateCubeBounds(cubes: Set<Triple<Int, Int, Int>>): Triple<IntRange, IntRange, IntRange> {
    var minX = Int.MAX_VALUE
    var maxX = Int.MIN_VALUE
    var minY = Int.MAX_VALUE
    var maxY = Int.MIN_VALUE
    var minZ = Int.MAX_VALUE
    var maxZ = Int.MIN_VALUE

    for(cube in cubes) {
        minX = minOf(minX, cube.first - 1)
        maxX = maxOf(maxX, cube.first + 1)
        minY = minOf(minY, cube.second - 1)
        maxY = maxOf(maxY, cube.second + 1)
        minZ = minOf(minZ, cube.third - 1)
        maxZ = maxOf(maxZ, cube.third + 1)
    }

    return Triple(
        minX..maxX, minY..maxY, minZ..maxZ
    )
}

fun calculateSurfaceArea(cubes: List<Triple<Int, Int, Int>>): List<Triple<Int, Int, Int>> {
    val cubeSet = cubes.toSet()
    val surfaceAreaCubes = arrayListOf<Triple<Int, Int, Int>>()
    for (cube in cubes) {
        for(adj in getAdjacentCubes(cube)) {
            if (adj !in cubeSet) {
                surfaceAreaCubes.add(adj)
            }
        }
    }
    return surfaceAreaCubes
}

fun getAdjacentCubes(cube: Triple<Int, Int, Int>): List<Triple<Int, Int, Int>> {
    return listOf(
        Triple(cube.first + 1, cube.second, cube.third),
        Triple(cube.first - 1, cube.second, cube.third),
        Triple(cube.first, cube.second + 1, cube.third),
        Triple(cube.first, cube.second - 1, cube.third),
        Triple(cube.first, cube.second, cube.third + 1),
        Triple(cube.first, cube.second, cube.third - 1)
    )
}
