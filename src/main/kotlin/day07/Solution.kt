package day07

import readLines
import java.lang.IllegalStateException
import java.util.function.Predicate

fun main() {
    val terminalOutput = readLines("/day07/input.txt")
    val rootDirectory = buildDirectory(terminalOutput)
    rootDirectory.calculateTotalSize()
    println("part1: ${rootDirectory.getDirectories {it.totalSize <= 100000}.sumOf { it.totalSize }}")

    val totalDiskSize = 70000000L
    val minimumNeededAvailableDiskSpace = 30000000L
    val unusedSize = totalDiskSize - rootDirectory.totalSize
    val minimumDiskSpaceToClean = minimumNeededAvailableDiskSpace - unusedSize

    println("part2: ${rootDirectory.getDirectories {it.totalSize >= minimumDiskSpaceToClean}.minOf { it.totalSize }}")
}

fun buildDirectory(terminalOutput: List<String>): AocFile {
    val rootDirectory = AocFile("/", true, 0L)
    val deque = ArrayDeque<AocFile>()
    for(line in terminalOutput) {
        if(isMovingIntoRootDirectory(line)) {
            deque.addLast(rootDirectory)
        } else if(isMovingIntoDirectory(line)) {
            val newDirectory = AocFile(line.split(" ").last(), true, 0L)
            deque.lastOrNull()?.addFile(newDirectory)
            deque.addLast(newDirectory)
        } else if(isBacktrackingToParentDirectory(line)) {
            deque.removeLast()
        } else if(isFileOrDirectoryListing(line)) {
            val file = parseFile(line)
            if(!file.isDirectory) {
                deque.lastOrNull()?.addFile(file)
            }
        }
    }
    return rootDirectory
}

fun isFileOrDirectoryListing(line: String): Boolean {
    return !line.startsWith("$")
}

fun isMovingIntoRootDirectory(line: String): Boolean {
    return line.startsWith("$ cd /")
}

fun parseFile(line: String): AocFile {
    val parts = line.split(" ")
    return if(parts[0] == "dir") {
        AocFile(parts[1], true, 0L)
    } else {
        AocFile(parts[1], false, parts[0].toLong())
    }
}

fun isMovingIntoDirectory(line: String): Boolean {
    return line.startsWith("$") && line.contains("cd") && !line.contains("..")
}

fun isBacktrackingToParentDirectory(line: String): Boolean {
    return line.startsWith("$") && line.contains("cd ..")
}

class AocFile(private val name: String, val isDirectory: Boolean, private val fileSize: Long) {
    private var files = arrayListOf<AocFile>()
    var totalSize = 0L

    fun addFile(file: AocFile) {
        if(!isDirectory) {
            throw IllegalStateException("Cannot add file: AocFile $name is not a directory")
        }
        files.add(file)
    }

    fun calculateTotalSize(): Long {
        totalSize = files.sumOf { it.calculateTotalSize() } + fileSize
        return totalSize
    }

    fun getDirectories(predicate: Predicate<AocFile>): List<AocFile> {
        val result = arrayListOf<AocFile>()
        if(predicate.test(this)) {
            result.add(this)
        }
        result.addAll(files.filter { it.isDirectory }.flatMap { it.getDirectories(predicate) })
        return result
    }

    override fun toString(): String {
        return "AocFile(name=${name}, total size=${totalSize}"
    }
}
