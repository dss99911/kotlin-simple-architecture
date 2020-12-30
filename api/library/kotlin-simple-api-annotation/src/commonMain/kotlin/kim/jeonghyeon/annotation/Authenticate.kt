package kim.jeonghyeon.annotation

/**
 *
 * in order to use this, use 'kotlin-simple-architecture-client/backend', if you use 'kotlin-simple-api-client/backend'
 *
 * designed name is different for signIn and for service apis.
 * for sigIn, there is BASIC, DIGEST, OAUTH
 * for service apis, there can be session, jwt token
 * developer just set name as empty. different name is used only in library.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
public annotation class Authenticate(val name: String = "")