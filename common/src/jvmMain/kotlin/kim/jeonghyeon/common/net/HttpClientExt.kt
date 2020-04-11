package kim.jeonghyeon.common.net

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature


inline fun <reified API> api(baseUrl: String): API = client.create(baseUrl)

val client = httpClientDefault()