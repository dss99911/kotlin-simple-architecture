package kim.jeonghyeon.annotation

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Query(val name: String, val encoded: Boolean = false)