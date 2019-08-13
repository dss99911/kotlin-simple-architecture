package kim.jeonghyeon.sample

fun main() {
    val matches = Regex("abc").matches("aaabcdfadsf")
    println(matches)
}