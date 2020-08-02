package kim.jeonghyeon.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class HeaderMap(val name: String)