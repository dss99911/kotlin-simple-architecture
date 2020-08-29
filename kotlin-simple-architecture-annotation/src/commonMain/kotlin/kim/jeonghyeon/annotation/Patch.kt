package kim.jeonghyeon.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
public annotation class Patch(val path: String)