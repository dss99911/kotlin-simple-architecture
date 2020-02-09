package kim.jeonghyeon.kotlinusecase.map

/**
 * make map from array
 */
fun testAssociateWith() {
    val keys = 'a'..'f'
    val map = keys.associateWith { it.toString().repeat(5).capitalize() }
    map.forEach { println(it) }

    /*
    a=Aaaaa
    b=Bbbbb
    c=Ccccc
    d=Ddddd
    e=Eeeee
    f=Fffff
     */
}