package kim.jeonghyeon.annotation

/**
 * use GET method,
 * @param path
 *  if "" then, baseUrl + Api's path
 *  if contains '://' then, replace baseUrl
 *  if it's path, baseUrl + Api's path + this
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Get(val path: String = "")