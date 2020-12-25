package kim.jeonghyeon.base

import base.generated.SimpleConfig
import base.generated.net.create
import io.ktor.client.*
import kim.jeonghyeon.net.httpClientSimple

val client: HttpClient by lazy {
    httpClientSimple {
        //add configuration
        //this is in commonMain as backend also can be client of other backend
    }
}

inline fun <reified API> api(baseUrl: String = "http://${SimpleConfig.buildTimeLocalIpAddress}:8080"): API = client.create(baseUrl)