package day11

import readChunkedLines

fun main() {
    var numberOfInspectedItemsPerMonkey = executeRounds(parseMonkeys(readChunkedLines("/day11/input.txt")), 20, "part1")
    println("part1: ${numberOfInspectedItemsPerMonkey.sortedDescending().take(2).fold(1L) { acc, item -> acc * item }}")

    numberOfInspectedItemsPerMonkey = executeRounds(parseMonkeys(readChunkedLines("/day11/input.txt")), 10000, "part2")
    println("part2: ${numberOfInspectedItemsPerMonkey.sortedDescending().take(2).fold(1L) { acc, item -> acc * item }}")
}

fun executeRounds(monkeys: List<Monkey>, numberOfRounds: Int, part: String): List<Int> {
    val numberOfInspectedItemsPerMonkey = arrayOfNulls<Int>(monkeys.size).map { 0 }.toMutableList()
    for(round in 0 until numberOfRounds) {
        for((index, monkey) in monkeys.withIndex()) {
            numberOfInspectedItemsPerMonkey[index] += monkey.inspectAndThrowItems(part)
        }
    }
    return numberOfInspectedItemsPerMonkey
}

fun parseMonkeys(monkeysInput: List<List<String>>): List<Monkey> {
    val parsedMonkeys = arrayListOf<Monkey>()
    for(monkeyToParse in monkeysInput) {
        val monkey = Monkey()
        monkey.items.addAll(monkeyToParse[1].trim().split(" ").drop(2)
            .map { it.removeSuffix(",").toLong() }
            .map { Expression(null, null, it, "identity") })
        monkey.operation = parseMonkeyOperation(monkeyToParse[2])
        monkey.divisibleBy = monkeyToParse[3].trim().split(" ")[3].toLong()
        parsedMonkeys.add(monkey)
    }
    for((index, monkeyToParse) in monkeysInput.withIndex()) {
        val indexOfMonkeyForSuccessfulTest = monkeyToParse[4].trim().split(" ")[5].toInt()
        val indexOfMonkeyForFailedTest = monkeyToParse[5].trim().split(" ")[5].toInt()
        parsedMonkeys[index].monkeyForSuccessfulTest = parsedMonkeys[indexOfMonkeyForSuccessfulTest]
        parsedMonkeys[index].monkeyForFailedTest = parsedMonkeys[indexOfMonkeyForFailedTest]
    }
    return parsedMonkeys
}

fun parseMonkeyOperation(operationInput: String): (Expression) -> Expression {
    val operation = operationInput.trim().split(" ")[4]
    val secondOperand = operationInput.trim().split(" ")[5]

    return when(operation) {
        "+" -> { old -> if(secondOperand == "old") Expression(old, old, -1, "+") else Expression(old, Expression(null, null, secondOperand.toLong(), "identity"), -1, "+")}
        "*" -> { old -> if(secondOperand == "old") Expression(old, old, -1, "*") else Expression(old, Expression(null, null, secondOperand.toLong(), "identity"), -1, "*")}
        else -> { old -> old}
    }
}

class Monkey {
    private val operationAfterInspection: (Expression) -> Expression = { expression -> Expression(expression, Expression(null, null, 3L,  "identity"), -1L, "/") }

    var items = arrayListOf<Expression>()
    var divisibleBy = 1L
    var monkeyForSuccessfulTest: Monkey? = null
    var monkeyForFailedTest: Monkey? = null
    var operation: (Expression) -> Expression = {e -> e}

    fun inspectAndThrowItems(part: String): Int {
        val sizeOfItems = items.size
        for(item in items) {
            val inspectedItem = if(part == "part1") {
                operationAfterInspection.invoke(operation.invoke(item))
            } else {
                operation.invoke(item)
            }
            if(isTestSuccessful(inspectedItem, part)) {
                monkeyForSuccessfulTest?.acceptItem(inspectedItem)
            } else {
                monkeyForFailedTest?.acceptItem(inspectedItem)
            }
        }
        items.clear()
        return sizeOfItems
    }

    private fun isTestSuccessful(item: Expression, part: String): Boolean {
        return if(part == "part1") {
            item.calculateExpression() % divisibleBy == 0L
        } else {
            item.calculateRemainder(divisibleBy) == 0L
        }
    }

    private fun acceptItem(inspectedItem: Expression) {
        items.add(inspectedItem)
    }

    override fun toString(): String {
        return """
            Monkey:
                items: $items
                divisible by: $divisibleBy
        """
    }
}

class Expression(private val left: Expression?, private val right: Expression?, private val identity: Long, private val operation: String) {
    private var expressionValue: Long? = null
    private val rems = hashMapOf<Long, Long>()

    fun calculateRemainder(n: Long): Long {
        if(n in rems) {
            return rems.getValue(n)
        }
        val rem = if(operation == "identity") {
            identity % n
        } else if(operation == "*" && left != null && right != null) {
            ((left.calculateRemainder(n) % n ) * (right.calculateRemainder(n)) % n) % n
        } else if(operation == "+" && left != null && right != null) {
            ((left.calculateRemainder(n) % n) + (right.calculateRemainder(n)) % n) % n
        } else {
            0L
        }
        rems[n] = rem
        return rem
    }

    fun calculateExpression(): Long {
        if(expressionValue != null) {
            return expressionValue as Long
        }

        expressionValue = if(operation == "identity") {
            identity
        } else if(operation == "*" && left != null && right != null) {
            left.calculateExpression() * right.calculateExpression()
        } else if(operation == "+" && left != null && right != null) {
            left.calculateExpression() + right.calculateExpression()
        } else if(operation == "/" && left != null && right != null){
            left.calculateExpression() / right.calculateExpression()
        } else {
            0L
        }
        return expressionValue as Long
    }
}
