package kim.jeonghyeon.net

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*

@HttpClientDsl
expect fun httpClientSimple(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient

@HttpClientDsl
fun httpClientDefault(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
        })
    }

    config()
}