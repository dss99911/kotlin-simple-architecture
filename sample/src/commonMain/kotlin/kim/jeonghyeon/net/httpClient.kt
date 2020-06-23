package kim.jeonghyeon.net

import io.ktor.client.HttpClient
import kim.jeonghyeon.generated.net.create

expect val client: HttpClient

inline fun <reified API> api(baseUrl: String): API = client.create(baseUrl)