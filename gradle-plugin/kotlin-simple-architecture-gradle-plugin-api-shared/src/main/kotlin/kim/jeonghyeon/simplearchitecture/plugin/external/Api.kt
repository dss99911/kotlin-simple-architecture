package kim.jeonghyeon.annotation


//todo https://hyun.myjetbrains.com/youtrack/issue/KSA-25
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Api(val path: String = "")