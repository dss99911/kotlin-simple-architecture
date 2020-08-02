package kim.jeonghyeon.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Query(val name: String, val encoded: Boolean = false)