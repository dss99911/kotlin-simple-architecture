package kim.jeonghyeon.net

import androidLibrary.sample.samplebase.generated.SimpleConfig
import androidLibrary.sample.samplebase.generated.net.create
import io.ktor.client.HttpClient
import io.ktor.client.features.cookies.CookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.*
import io.ktor.util.InternalAPI
import io.ktor.util.date.GMTDate
import io.ktor.util.toLowerCasePreservingASCIIRules
import kim.jeonghyeon.kotlinsimplearchitecture.generated.net.createSimple
import kim.jeonghyeon.pergist.Preference
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable

const val HEADER_KEY = "KEY"

val client: HttpClient = httpClientSimple {
    defaultRequest {
        //this is called whenever api is called
        header(HEADER_KEY, headerKeyValue)
    }

    //todo is this proper approach? I used cookies because when we support js, cookie may be simpler.
    // this is not fixed approach. let's consider to use header.
    install(HttpCookies) {
        // Will keep an in-memory map with all the cookies from previous requests.
        storage = TokenStorage()
    }
}

val serverUrl: String = "http://${SimpleConfig.BUILD_TIME_LOCAL_IP_ADDRESS}:8080"

inline fun <reified API> api(baseUrl: String = serverUrl): API = client.create(baseUrl)
inline fun <reified API> apiSimple(baseUrl: String = serverUrl): API = client.createSimple(baseUrl)


var headerKeyValue = "Header test"

val Preference.KEY_USER_COOKIE get() = "USER_COOKIE"
const val USER_COOKIE_NAME = "simple-user"

/**
 * todo save requestUrl also. so that. get cookie from the url and save cookie to the url
 */
class TokenStorage(val preference: Preference = Preference()) : CookiesStorage {
    @ImplicitReflectionSerializer
    var cache: Cookie? = preference.get<SerializableCookie>(preference.KEY_USER_COOKIE)?.toCookie()

    @InternalAPI
    @ImplicitReflectionSerializer
    override suspend fun get(requestUrl: Url): List<Cookie> {
        cache ?: return emptyList()

        if (cache?.matches(requestUrl) != true) {
            return emptyList()
        }

        val now = GMTDate()
        val expires = cache?.expires?.timestamp
        if (expires != null && expires < now.timestamp) {
            cache = null
            preference.setString(preference.KEY_USER_COOKIE, null)
            return emptyList()
        }

        return listOf(cache!!)
    }

    @ImplicitReflectionSerializer
    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        if (cookie.name != USER_COOKIE_NAME) return

        //todo check url of cookie
        val updatedCookie = cookie.fillDefaults(requestUrl)
        preference.set(preference.KEY_USER_COOKIE, updatedCookie.toSerializable())
        cache = updatedCookie
    }

    override fun close() {

    }
}

@Serializable
data class SerializableCookie(
    val name: String,
    val value: String,
    val encoding: String = CookieEncoding.URI_ENCODING.name,
    val maxAge: Int = 0,
    val expires: Long? = null,
    val domain: String? = null,
    val path: String? = null,
    val secure: Boolean = false,
    val httpOnly: Boolean = false,
    val extensions: Map<String, String?> = emptyMap()
) {
    fun toCookie() = Cookie(
        name,
        value,
        CookieEncoding.valueOf(encoding),
        maxAge,
        expires?.let { GMTDate(it) },
        domain,
        path,
        secure,
        httpOnly,
        extensions
    )
}

fun Cookie.toSerializable() = SerializableCookie(
    name,
    value,
    encoding.name,
    maxAge,
    expires?.timestamp,
    domain,
    path,
    secure,
    httpOnly,
    extensions
)

@InternalAPI
fun Cookie.matches(requestUrl: Url): Boolean {
    val domain = domain?.toLowerCasePreservingASCIIRules()?.trimStart('.')
        ?: error("Domain field should have the default value")

    val path = with(path) {
        val current = path ?: error("Path field should have the default value")
        if (current.endsWith('/')) current else "$path/"
    }

    val host = requestUrl.host.toLowerCasePreservingASCIIRules()
    val requestPath = let {
        val pathInRequest = requestUrl.encodedPath
        if (pathInRequest.endsWith('/')) pathInRequest else "$pathInRequest/"
    }

    if (host != domain && (hostIsIp(host) || !host.endsWith(".$domain"))) {
        return false
    }

    if (path != "/" &&
        requestPath != path &&
        !requestPath.startsWith(path)
    ) return false

    return !(secure && !requestUrl.protocol.isSecure())
}

fun Cookie.fillDefaults(requestUrl: Url): Cookie {
    var result = this

    //todo Cookie.path is '/' on http://192.168.224.95:8080/kim/jeonghyeon/sample/api/UserApi/getUser
    // when it is not '/'?
    // anyway, path should be / so that apply to all path of the domain
    if (result.path?.startsWith("/") != true) {
        result = result.copy(path = requestUrl.encodedPath)
    }

    if (result.domain.isNullOrBlank()) {
        result = result.copy(domain = requestUrl.host)
    }

    return result
}
