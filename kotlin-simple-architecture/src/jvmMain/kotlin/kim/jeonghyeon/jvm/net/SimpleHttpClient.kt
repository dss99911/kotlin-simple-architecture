package kim.jeonghyeon.jvm.net

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kim.jeonghyeon.common.net.throwException
import kim.jeonghyeon.common.net.validateResponse
import kim.jeonghyeon.jvm.extension.toJsonObject
import kim.jeonghyeon.jvm.reflect.suspendProxy
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

inline fun <reified T> HttpClient.create(baseUrl: String) =
    suspendProxy(T::class.java) { method, arguments ->
        val mainPath = T::class.java.name.replace(".", "-")
        val subPath = method.name
        val baseUrlWithoutSlash = if (baseUrl.last() == '/') baseUrl.take(baseUrl.lastIndex) else baseUrl
        val returnType = method.kotlinFunction!!.returnType

        val response = try {
            post<HttpResponse>("$baseUrlWithoutSlash/$mainPath/$subPath") {
                contentType(ContentType.Application.Json)

                //can not use kotlin serialization.
                //type mismatch. required: capturedtype(out any). found: any
                body = arguments
            }
        } catch (e: Exception) {
            throwException(e)
        }
        validateResponse(response)

        if (returnType.classifier == Unit::class) {
            Unit
        } else {
            response.readText().toJsonObject<Any?>(returnType.javaType)
        }
    }