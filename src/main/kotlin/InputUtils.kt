import java.io.File

fun readLines(input: String): List<String> {
    return File(object {}.javaClass.getResource(input).toURI()).bufferedReader().readLines()
}

fun readLinesAsInt(input: String): List<Int> {
    return readLines(input).map { it.toInt() }
}