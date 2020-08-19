package kim.jeonghyeon.jvm.net

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kim.jeonghyeon.annotation.Authenticate
import kim.jeonghyeon.extension.replaceLast
import kim.jeonghyeon.jvm.extension.toJsonObject
import kim.jeonghyeon.jvm.reflect.suspendProxy
import kim.jeonghyeon.net.fetchResponseText
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

/**
 * available only on JVM
 * - no need to add @Api annotation on api interface
 * - no need to add @Serializable on request and response body
 *
 */
@Deprecated("this is not maintained. as it's available only on JVM", ReplaceWith("@Api"))
inline fun <reified T> HttpClient.create(baseUrl: String) =
    suspendProxy(T::class.java) { method, arguments ->
        val mainPath = T::class.java.name
            .replace(".", "-")
            .replaceLast("-", "/")

        val subPath = method.name
        val baseUrlWithoutSlash = if (baseUrl.last() == '/') baseUrl.take(baseUrl.lastIndex) else baseUrl
        val returnType = method.kotlinFunction!!.returnType

        val responseText = fetchResponseText(method.annotations.any { it is Authenticate }) { adder ->
            post<HttpResponse>("$baseUrlWithoutSlash/$mainPath/$subPath") {
                contentType(ContentType.Application.Json)
                adder()
                //can not use kotlin serialization.
                //type mismatch. required: capturedtype(out any). found: any
                body = arguments
            }
        }

        if (returnType.classifier == Unit::class) {
            Unit
        } else {
            responseText.toJsonObject<Any?>(returnType.javaType)
        }
    }