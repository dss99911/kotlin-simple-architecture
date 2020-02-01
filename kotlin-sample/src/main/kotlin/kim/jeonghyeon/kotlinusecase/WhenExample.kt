package kim.jeonghyeon.kotlinusecase

fun whenExample(c1: String, c2: String) = when (setOf(c1, c2)) {
    setOf("1", "2") -> 1
    setOf("2", "3") -> 2
    setOf("3", "1") -> 3
    else -> error("test")
}