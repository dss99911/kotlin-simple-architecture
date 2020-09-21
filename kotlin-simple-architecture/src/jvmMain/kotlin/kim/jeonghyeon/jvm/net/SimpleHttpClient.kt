package kim.jeonghyeon.jvm.net

/**
 * available only on JVM
 * - no need to add @Api annotation on api interface
 * - no need to add @Serializable on request and response body
 *
 */
//todo delete?
//@Deprecated("this is not maintained. as it's available only on JVM", ReplaceWith("@Api"))
//inline fun <reified T> HttpClient.create(baseUrl: String) = SimpleApiUtil.run {
//    suspendProxy(T::class.java) { method, arguments ->
//        val mainPath = T::class.java.name
//            .replace(".", "-")
//            .replaceLast("-", "/")
//
//        val subPath = method.name
//        val baseUrlWithoutSlash =
//            if (baseUrl.last() == '/') baseUrl.take(baseUrl.lastIndex) else baseUrl
//        val returnType = method.kotlinFunction!!.returnType
//
//        val callInfo = ApiCallInfo(baseUrlWithoutSlash, mainPath, subPath, HttpMethod.Post,
//            listOf(
//                ApiParameterInfo(ApiParameterType.BODY, null, arguments),
//                )
//        )
//
//          // how to set return type?
//        return callApi<ResultType>(method.annotations.any { it is Authenticate }, callInfo)
//
//    }
//}