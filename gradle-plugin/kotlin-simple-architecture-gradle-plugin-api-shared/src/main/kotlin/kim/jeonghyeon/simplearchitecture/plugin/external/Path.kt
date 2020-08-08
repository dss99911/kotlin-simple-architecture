package kim.jeonghyeon.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Path(val name: String, val encoded: Boolean = false)