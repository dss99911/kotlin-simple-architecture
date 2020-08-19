package kim.jeonghyeon.net

import androidLibrary.sample.samplebase.generated.SimpleConfig
import androidLibrary.sample.samplebase.generated.net.create
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.features.HttpSend
import io.ktor.client.features.auth.Auth
import io.ktor.client.features.auth.AuthProvider
import io.ktor.client.features.auth.providers.digest
import io.ktor.client.features.cookies.CookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.feature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.http.*
import io.ktor.http.auth.parseAuthorizationHeader
import io.ktor.util.AttributeKey
import io.ktor.util.InternalAPI
import io.ktor.util.date.GMTDate
import io.ktor.util.toLowerCasePreservingASCIIRules
import kim.jeonghyeon.auth.AuthenticationType
import kim.jeonghyeon.kotlinsimplearchitecture.generated.net.createSimple
import kim.jeonghyeon.pergist.Preference
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable

const val HEADER_KEY = "KEY"

val AUTH_TYPE = AuthenticationType.DIGEST

val client: HttpClient by lazy {
    httpClientSimple {
        defaultRequest {
            //this is called whenever api is called
            header(HEADER_KEY, headerKeyValue)
        }
    }
}

val serverUrl: String = "http://${SimpleConfig.BUILD_TIME_LOCAL_IP_ADDRESS}:8080"

inline fun <reified API> api(baseUrl: String = serverUrl): API = client.create(baseUrl)
inline fun <reified API> apiSimple(baseUrl: String = serverUrl): API = client.createSimple(baseUrl)


var headerKeyValue = "Header test"