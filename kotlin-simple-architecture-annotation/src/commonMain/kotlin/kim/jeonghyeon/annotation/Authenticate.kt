package kim.jeonghyeon.annotation

/**
 * designed name is different for signIn and for service apis.
 * for sigIn, there is BASIC, DIGEST, OAUTH
 * for service apis, there can be session, jwt token
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
public annotation class Authenticate(val name: String = "")