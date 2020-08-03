package kim.jeonghyeon.annotation

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Path(val name: String, val encoded: Boolean = false)