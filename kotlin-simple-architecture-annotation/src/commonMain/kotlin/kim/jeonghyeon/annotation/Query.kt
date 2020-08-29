package kim.jeonghyeon.annotation

/**
 * todo support encoded
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
public annotation class Query(val name: String, val encoded: Boolean = false)