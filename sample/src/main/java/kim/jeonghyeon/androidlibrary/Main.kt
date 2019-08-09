package kim.jeonghyeon.androidlibrary

fun main() {
    val matches = Regex("abc").matches("aaabcdfadsf")
    println(matches)
}