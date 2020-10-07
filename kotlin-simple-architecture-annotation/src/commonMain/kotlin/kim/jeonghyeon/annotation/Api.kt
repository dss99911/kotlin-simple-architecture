package kim.jeonghyeon.annotation


/**
 * add annotation on interface.
 * @param path if it's "" then use base url, if it contains ://, replace base url. if it contains some path, attach to base url
 *
 * todo this retention is runtime becase backend jvm use annotation on runtime. if backend migrate to native, this is not required
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
public annotation class Api(val path: String = "")