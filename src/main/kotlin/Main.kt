import java.util.*

fun main() {
    println("Advent Of Code 2022")
    println("Run a solution for a day in console using gradle:")
    val os = System.getProperty("os.name").lowercase(Locale.getDefault())
    when {
        os.contains("win") -> println("Example: gradlew.bat -Pday=1")
        else -> println("Example: ./gradlew -Pday=1")
    }
}
