package kim.jeonghyeon.base

import base.generated.net.create
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*

val client: HttpClient by lazy {
    HttpClient {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }
}

inline fun <reified API> api(baseUrl: String = "{server-url}}:8080"): API = client.create(baseUrl)