package kim.jeonghyeon.annotation


/**
 * add annotation on interface.
 * @param path if it's "" then use base url, if it contains ://, replace base url. if it contains some path, attach to base url
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Api(val path: String = "")