package kim.jeonghyeon.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class QueryMap(val encoded: Boolean = false)