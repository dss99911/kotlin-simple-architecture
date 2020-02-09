package kim.jeonghyeon.kotlinusecase.annotation

object JavaCompatibility {

    /**
     * without @JvmStatic.
     * you should call as JavaCompatibility.INSTANCE.intListToString()
     * with @JvmStatic, you can use the below
     * JavaCompatibility.intListToString()
     */
    @JvmStatic
    fun intListToString(ints: List<Int>?): String? {
        return ints?.joinToString(",")
    }
}
