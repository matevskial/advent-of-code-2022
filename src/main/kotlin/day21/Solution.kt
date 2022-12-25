package day21

import readLines
import java.lang.IllegalStateException

fun main() {
    val expressions = readLines("/day21/input.txt").map { parseExpression(it) }
    val expressionsByName = expressions.associateBy { it.name }
    for(expression in expressions) {
        if (expression is BinaryExpression) {
            expression.leftExpression = expressionsByName[expression.leftExpressionName]
            expression.rightExpression = expressionsByName[expression.rightExpressionString]
        }
    }

    val rootExpression = expressionsByName.getValue("root") as BinaryExpression
    val part1 = rootExpression.evaluate()
    println("part1: $part1")

    // the assumption for part 2 is that only one subexpression of each binary expression will depend on the value of humn expression
    val expressionThatDependsOfHumn = if(rootExpression.leftExpression!!.hasNameOrChildWithName(Expression.HUMN)) rootExpression.leftExpression else rootExpression.rightExpression
    val expressionThatDoesntDependOfHumn = if(rootExpression.leftExpression!!.hasNameOrChildWithName(Expression.HUMN)) rootExpression.rightExpression else rootExpression.leftExpression
    val part2 = expressionThatDependsOfHumn!!.calculateHumnNumber(expressionThatDoesntDependOfHumn!!.evaluate())
    println("part2: $part2")
}

fun parseExpression(expressionInput: String): Expression {
    val parts = expressionInput.split(":").map { it.trim() }
    if(parts[1].toLongOrNull() != null) {
        return ValueExpression(parts[0], parts[1].toLong())
    }
    val operationSignRegex = "[+\\-*/]".toRegex()
    val expressionNames = parts[1].split("[+\\-*/]".toRegex()).map { it.trim() }
    val operationSign = operationSignRegex.find(parts[1])!!.value
    return BinaryExpression(parts[0], expressionNames[0], expressionNames[1], ExpressionOperation.fromSign(operationSign))
}

abstract class Expression(val name: String) {

    companion object {
        const val HUMN = "humn"
    }

    abstract fun evaluate(): Long
    abstract fun hasNameOrChildWithName(name: String): Boolean
    abstract fun calculateHumnNumber(value: Long): Long
}

class ValueExpression(name: String, var value: Long): Expression(name) {

    override fun evaluate(): Long {
        return value
    }

    override fun hasNameOrChildWithName(name: String): Boolean {
        return this.name == name
    }

    override fun calculateHumnNumber(value: Long): Long {
        if(name == HUMN) {
            return value
        }
        throw IllegalStateException("Assumption incorrect")
    }
}

class BinaryExpression(name: String, val leftExpressionName: String, val rightExpressionString: String, var operation: ExpressionOperation): Expression(name) {

    var leftExpression: Expression? = null
    var rightExpression: Expression? = null
    private var cachedHasNameOrChildWithNameHumn: Boolean? = null

    override fun evaluate(): Long {
        val leftExpressionValue = leftExpression!!.evaluate()
        val rightExpressionValue = rightExpression!!.evaluate()
        val result = when(operation) {
            ExpressionOperation.PLUS -> leftExpressionValue + rightExpressionValue
            ExpressionOperation.MINUS -> leftExpressionValue - rightExpressionValue
            ExpressionOperation.TIMES -> leftExpressionValue * rightExpressionValue
            ExpressionOperation.DIV -> leftExpressionValue / rightExpressionValue
        }
        return result
    }

    override fun hasNameOrChildWithName(name: String): Boolean {
        if(cachedHasNameOrChildWithNameHumn != null) {
            return cachedHasNameOrChildWithNameHumn!!
        }
        if(this.name == name) {
            return true
        }
        cachedHasNameOrChildWithNameHumn = leftExpression!!.hasNameOrChildWithName(name) || rightExpression!!.hasNameOrChildWithName(name)
        return cachedHasNameOrChildWithNameHumn!!
    }

    override fun calculateHumnNumber(value: Long): Long {
        // the assumption for part 2 is that only one subexpression of each binary expression will depend on the value of humn expression
        val expressionThatDependsOfHumn = if(leftExpression!!.hasNameOrChildWithName(HUMN)) leftExpression else rightExpression
        val expressionThatDoesntDependOfHumn = if(leftExpression!!.hasNameOrChildWithName(HUMN)) rightExpression else leftExpression

        if(leftExpression!!.hasNameOrChildWithName(HUMN) == rightExpression!!.hasNameOrChildWithName(HUMN)) {
            throw IllegalStateException("Assumption incorrect")
        }

        val fixedValue = expressionThatDoesntDependOfHumn!!.evaluate()
        val nextValue = when(operation) {
            ExpressionOperation.PLUS -> value - fixedValue
            ExpressionOperation.MINUS -> if(expressionThatDoesntDependOfHumn == leftExpression) fixedValue - value else value + fixedValue
            ExpressionOperation.TIMES -> value / fixedValue
            ExpressionOperation.DIV -> if(expressionThatDoesntDependOfHumn == leftExpression) fixedValue / value else value * fixedValue
        }
        return expressionThatDependsOfHumn!!.calculateHumnNumber(nextValue)
    }
}

enum class ExpressionOperation(private val operationSign: String) {
    PLUS("+"), MINUS("-"), TIMES("*"), DIV("/");

    companion object {
        fun fromSign(operationSign: String): ExpressionOperation {
            return ExpressionOperation.values().first { it.operationSign == operationSign }
        }
    }
}
