package kim.jeonghyeon.net

import androidLibrary.sample.samplebase.generated.net.create
import io.ktor.client.HttpClient
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.header

const val HEADER_KEY = "KEY"

val client: HttpClient get() = httpClientSimple {
    defaultRequest {
        //this is called whenever api is called
        header(HEADER_KEY, headerKeyValue)
    }
}

inline fun <reified API> api(baseUrl: String): API = client.create(baseUrl)

var headerKeyValue = "Header test"