package kim.jeonghyeon.sample.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kim.jeonghyeon.net.ApiCallInfo
import kim.jeonghyeon.net.RequestResponseAdapter
import kotlin.reflect.full.isSubclassOf

/**
 * migrate your Retrofit Call Aapter to this function
 */
inline fun getCustomApiAdapter(): RequestResponseAdapter = object : RequestResponseAdapter() {
    override suspend fun <OUT> transformResponse(
        response: HttpResponse,
        callInfo: ApiCallInfo,
        returnTypeInfo: TypeInfo
    ): OUT {
        return if (returnTypeInfo.type == RetrofitResponseBody::class) {
            response.call.receive(returnTypeInfo) as OUT
        } else if (returnTypeInfo.type.isSubclassOf(Data::class)){
            (response.call.receive(typeInfo<RetrofitResponseBody<OUT>>()) as RetrofitResponseBody<OUT>).data
        } else {
            error("not supported")
        }

        //other jvm example case from actual project
//        return if (returnTypeInfo.type.isSubclassOf(BaseResponse::class)) {
//            response.call.receive(returnTypeInfo) as OUT
//        } else if (returnTypeInfo.type.isSubclassOf(BaseData::class)) {
//            val createType: KType = BaseResponseV2::class.createType(
//                listOf(
//                    KTypeProjection(
//                        KVariance.INVARIANT,
//                        returnTypeInfo.kotlinType
//                    )
//                )
//            )
//            (response.call.receive(
//                TypeInfo(
//                    BaseResponse::class,
//                    createType.javaType,
//                    createType
//                )
//            ) as BaseResponse<BaseData>).data as OUT
//        } else {
//            error("Not supported type")
//        }
    }
}