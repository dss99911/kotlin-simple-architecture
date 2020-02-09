package kim.jeonghyeon.kotlinusecase.collection

/**
 * Shuffled items: [1, 5, 2, 4, 3]
Items doubled: [2, 10, 4, 8, 6]
Items filled with 5: [5, 5, 5, 5, 5]

 */
fun test2() {
    val items = (1..5).toMutableList()

    items.shuffle()
    println("Shuffled items: $items")

    items.replaceAll { it * 2 }
    println("Items doubled: $items")

    items.fill(5)
    println("Items filled with 5: $items")
}