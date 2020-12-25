package kim.jeonghyeon.annotation

/**
 * designed name is different for signIn and for service apis.
 * for sigIn, there is BASIC, DIGEST, OAUTH
 * for service apis, there can be session, jwt token
 * developer just set name as empty. different name is used only in library.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
public annotation class Authenticate(val name: String = "")