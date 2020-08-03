package kim.jeonghyeon.annotation

/**
 * todo support encoded
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Path(val name: String, val encoded: Boolean = false)