import java.io.File

fun readLines(input: String): List<String> {
    return File(object {}.javaClass.getResource(input).toURI()).bufferedReader().readLines()
}

fun readChunkedLines(input:String): List<List<String>> {
    val chunked = arrayListOf<List<String>>()
    val lines = readLines(input)
    var currentChunk = arrayListOf<String>()
    for ((i, v) in lines.withIndex()) {
        if(v.isBlank() || i == lines.size) {
            if(currentChunk.isNotEmpty()) {
                chunked.add(currentChunk)
                currentChunk = arrayListOf()
            }
        } else {
            currentChunk.add(v)
        }
    }
    return chunked
}

fun readLinesAsInt(input: String): List<Int> {
    return readLines(input).map { it.toInt() }
}
