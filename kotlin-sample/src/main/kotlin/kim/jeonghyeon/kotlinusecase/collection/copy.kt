package kim.jeonghyeon.kotlinusecase.collection

fun testCopy() {
    val sourceArr = arrayOf("k", "o", "t", "l", "i", "n")
    val targetArr = sourceArr.copyInto(arrayOfNulls<String>(6), 3, startIndex = 3, endIndex = 6)
    println(targetArr.contentToString())

    sourceArr.copyInto(targetArr, startIndex = 0, endIndex = 3)
    println(targetArr.contentToString())

    //[null, null, null, l, i, n]
    //[k, o, t, l, i, n]
}