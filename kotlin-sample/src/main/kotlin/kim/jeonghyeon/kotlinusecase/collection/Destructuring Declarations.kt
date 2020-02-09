package kim.jeonghyeon.kotlinusecase.collection

fun destructuring() {
    val items = (1..9).map { it * it }
    val (a, b, c) = items
}