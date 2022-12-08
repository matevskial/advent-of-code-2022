package day08

import readLines
import kotlin.math.abs

enum class Dir {
    BACKWARD, FORWARD
}

fun main() {
    val grid = readLines("/day08/input.txt").map { it.map { tree -> tree.digitToInt() } }
    
    val numberOfVisibleTrees = calculateNumberOfVisibleTrees(grid)
    println("part1: $numberOfVisibleTrees")
    
    val maxScenicScore = grid.flatMapIndexed {
            rowIndex, row -> row.mapIndexed {
                colIndex, _ -> calculateScenicScore(rowIndex, colIndex, grid)
            }
    }.maxOrNull()
    println("part2: $maxScenicScore")
}

fun calculateScenicScore(row: Int, col: Int, grid: List<List<Int>>): Int {
    return (calculateTreeViewingDistanceVertically(row, col, grid, Dir.FORWARD)
            * calculateTreeViewingDistanceVertically(row, col, grid, Dir.BACKWARD)
            * calculateTreeViewingDistanceHorizontally(row, col, grid, Dir.FORWARD)
            * calculateTreeViewingDistanceHorizontally(row, col, grid, Dir.BACKWARD))
}

fun calculateNumberOfVisibleTrees(grid: List<List<Int>>): Int {
    return grid
        .flatMapIndexed { rowIndex, row -> row.mapIndexed { colIndex, _ -> isTreeVisible(rowIndex, colIndex, grid) } }
        .count { it }
}

fun isTreeVisible(row: Int, col: Int, grid: List<List<Int>>): Boolean {
    return isTreeVisibleHorizontally(row, col, grid, Dir.BACKWARD) || isTreeVisibleHorizontally(row, col, grid, Dir.FORWARD)
            || isTreeVisibleVertically(row, col, grid, Dir.BACKWARD) || isTreeVisibleVertically(row, col, grid, Dir.FORWARD)
}

fun isTreeVisibleVertically(row: Int, col: Int, grid: List<List<Int>>, dir: Dir): Boolean {
    val indices = if(dir == Dir.FORWARD) row + 1 until grid.size else row - 1 downTo 0
    for(currentRow in indices) {
        if(grid[currentRow][col] >= grid[row][col]) {
            return false
        }
    }
    return true
}

fun calculateTreeViewingDistanceVertically(row: Int, col: Int, grid: List<List<Int>>, dir: Dir): Int {
    val indices = if(dir == Dir.FORWARD) row + 1 until grid.size else row - 1 downTo 0
    for(currentRow in indices) {
        if(grid[currentRow][col] >= grid[row][col]) {
            return abs(currentRow - row)
        }
    }
    return if(dir == Dir.FORWARD) abs(row - grid.size) - 1 else row
}

fun calculateTreeViewingDistanceHorizontally(row: Int, col: Int, grid: List<List<Int>>, dir: Dir): Int {
    val indices = if(dir == Dir.FORWARD) col + 1 until grid[row].size else col - 1 downTo 0
    for(currentCol in indices) {
        if(grid[row][currentCol] >= grid[row][col]) {
            return abs(currentCol - col)
        }
    }
    return if(dir == Dir.FORWARD) abs(col - grid[row].size) - 1 else col
}

fun isTreeVisibleHorizontally(row: Int, col: Int, grid: List<List<Int>>, dir: Dir): Boolean {
    val indices = if(dir == Dir.FORWARD) col + 1 until grid[row].size else col - 1 downTo 0
    for(currentCol in indices) {
        if(grid[row][currentCol] >= grid[row][col]) {
            return false
        }
    }
    return true
}
