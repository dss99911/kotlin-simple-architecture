package kim.jeonghyeon.net

import androidLibrary.sample.samplebase.generated.net.create
import io.ktor.client.HttpClient

expect val client: HttpClient

inline fun <reified API> api(baseUrl: String): API = client.create(baseUrl)