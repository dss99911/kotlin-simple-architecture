package kim.jeonghyeon.kotlinusecase.collection

/**
 * items: [1, 4, 9, 16, 25, 36, 49, 64, 81]

chunked into lists: [[1, 4, 9, 16], [25, 36, 49, 64], [81]]
3D points: [(1, 4, 9), (16, 25, 36), (49, 64, 81)]
windowed by 4: [[1, 4, 9, 16], [4, 9, 16, 25], [9, 16, 25, 36], [16, 25, 36, 49], [25, 36, 49, 64], [36, 49, 64, 81]]
sliding average by 4: [7.5, 13.5, 21.5, 31.5, 43.5, 57.5]
pairwise differences: [3, 5, 7, 9, 11, 13, 15, 17]
 */
fun test() {
    val items = (1..9).map { it * it }

    val chunkedIntoLists = items.chunked(4)
    val points3d = items.chunked(3) { (x, y, z) -> Triple(x, y, z) }
    val windowed = items.windowed(4)
    val slidingAverage = items.windowed(4) { it.average() }
    val pairwiseDifferences = items.zipWithNext { a, b -> b - a }
}