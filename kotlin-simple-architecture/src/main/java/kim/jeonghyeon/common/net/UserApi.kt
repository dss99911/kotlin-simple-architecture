//package kim.jeonghyeon.common.net
//
//import kim.jeonghyeon.annotation.Api
//
//@Api
//interface UserApi {
//    suspend fun action1()
//    suspend fun action2(name: String)
//    suspend fun action3(name: String)
//    suspend fun a(a: HashMap<String, Int>)
//}
//
//
//val apis = mutableListOf<Any>()
//
//fun main() {
//    val api = api<UserApi>("")
//
////    UserApi(clientAndroid, "")
//}
//
////class UserApiImpl2(
////    val client: HttpClient,
////    val baseUrl: String
////) : UserApi {
////    override suspend fun action1() {
////        val mainPath = "kim_jeonghyeon_common.net_UserApiImpl2"
////        val subPath = "action1"
////        val baseUrlWithoutSlash = if (baseUrl.last() == '/') baseUrl.take(baseUrl.lastIndex) else baseUrl
////
////        val response = try {
////            post<HttpResponse>("$baseUrlWithoutSlash/$mainPath/$subPath") {
////                contentType(ContentType.Application.Json)
////
////                //can not use kotlin serialization.
////                //type mismatch. required: capturedtype(out any). found: any
////                body = arguments
////            }
////        } catch (e: Exception) {
////            throwException(e)
////        }
////        kim.jeonghyeon.common.net.validateResponse(response)
////
////        if (hasReturn Type) {
////            response.readText().toJsonObject<Any?>(returnType.javaType)
////        }
////    }
////
////    override suspend fun action2(name: String) {
////    }
////
////    override suspend fun action3(name: String) {
////    }
////
////    override suspend fun a(a: HashMap<String, Int>) {
////    }
////}
