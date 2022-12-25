package day20

import readLinesAsInt
import kotlin.math.abs

fun main() {
    val encryptedFile = readLinesAsInt("/day20/input.txt").map { it.toLong() }
    var sumOfGroveCoordinates = calculateSumOfGroveCoordinates(encryptedFile, 1, 1)
    println("part1: $sumOfGroveCoordinates")

    sumOfGroveCoordinates = calculateSumOfGroveCoordinates(encryptedFile, 811589153, 10)
    println("part2: $sumOfGroveCoordinates")
}

fun calculateSumOfGroveCoordinates(encryptedFile: List<Long>, decryptionKey: Long, times: Int): Long {
    val linkedList = buildCircularLinkedList(encryptedFile)
    val linkedListNodes = buildArrayOfLinkedListNodes(linkedList, encryptedFile.size)
    linkedListNodes.forEach { it.value *= decryptionKey }
    val zeroNode = linkedListNodes.first { it.value == 0L }

    for (count in 0 until times) {
        for (node in linkedListNodes) {
            val timesToMove = (abs(node.value) % (encryptedFile.size - 1)).toInt()
            if(node.value > 0) {
                moveNodeRight(node, timesToMove)
            } else {
                moveNodeLeft(node, timesToMove)
            }
        }
    }

    var result = 0L
    var current = zeroNode
    for (i in 1..3000) {
        current = current.next!!
        if(i == 1000 || i == 2000 || i == 3000) {
            result += current.value
        }
    }

    return result
}

fun moveNodeRight(node: LinkedListNode, timesToMove: Int) {
    val currentNode = node
    for (count in 0 until timesToMove) {
        val currentPreviousNode = currentNode.previous
        val currentNextNode = currentNode.next
        if(currentPreviousNode != null) {
            currentPreviousNode.next = currentNextNode
        }
        if(currentNextNode != null) {
            currentNextNode.previous = currentPreviousNode
            val nextNextNode = currentNextNode.next
            currentNode.next = nextNextNode
            if(nextNextNode != null) {
                nextNextNode.previous = currentNode
            }
            currentNextNode.next = currentNode
        }
        currentNode.previous = currentNextNode
    }
}

fun moveNodeLeft(node: LinkedListNode, timesToMove: Int) {
    val currentNode = node
    for (count in 0 until timesToMove) {
        val currentPreviousNode = currentNode.previous
        val currentNextNode = currentNode.next
        if(currentNextNode != null) {
            currentNextNode.previous = currentPreviousNode
        }
        if(currentPreviousNode != null) {
            currentPreviousNode.next = currentNextNode
            val previousPreviousNode = currentPreviousNode.previous
            currentNode.previous = previousPreviousNode
            if(previousPreviousNode != null) {
                previousPreviousNode.next = currentNode
            }
            currentPreviousNode.previous = currentNode
        }
        currentNode.next = currentPreviousNode
    }
}

fun buildArrayOfLinkedListNodes(linkedList: LinkedListNode?, numberOfNodes: Int): List<LinkedListNode> {
    val linkedListNodes = arrayListOf<LinkedListNode>()
    var current = linkedList
    for (nodeNumber in 0 until numberOfNodes) {
        if(current == null) {
            break
        }
        linkedListNodes.add(current)
        current = current.next
    }
    return linkedListNodes
}

fun buildCircularLinkedList(numbers: List<Long>): LinkedListNode {
    val dummy = LinkedListNode(69, null, null)
    var current = dummy
    for (number in numbers) {
        val node = LinkedListNode(number, current, null)
        current = node
    }
    current.next = dummy.next
    dummy.next?.previous = current
    return if(dummy.next == null) dummy else dummy.next!!
}

class LinkedListNode(var value: Long, var previous: LinkedListNode?, var next: LinkedListNode?) {

    init {
        if(previous != null) {
            previous!!.next = this
        }

        if(next != null) {
            next!!.previous = this
        }
    }
}
