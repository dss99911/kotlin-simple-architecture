package kim.jeonghyeon.sample.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kim.jeonghyeon.net.ApiCallInfo
import kim.jeonghyeon.net.RequestResponseAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import samplebase.generated.SimpleConfig
import samplebase.generated.net.create
import kotlin.reflect.KType


interface RetrofitApi {
    /**
     * return by Response
     */
    @GET("retrofit/{key}/value")
    suspend fun getValue(@Path("key") key: String): RetrofitResponseBody<RetrofitData>

    /**
     * return by data
     */
    @POST("retrofit/{key}/value")
    suspend fun setValue(@Path("key") key: String, @Body body: RetrofitRequestBody): RetrofitData
}

data class RetrofitRequestBody(val value: String)

data class RetrofitResponseBody<T>(val data: T)

data class RetrofitData(val name: String, val id: String)

fun getRetrofitApi(): RetrofitApi {
    val okHttpClient = OkHttpClient()
    val client = okHttpClient.newBuilder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()
    val retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(SimpleConfig.serverUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(RetrofitApi::class.java)
}


fun getRetrofitApiFromSimpleApi(): RetrofitApi {
    return HttpClient(OkHttp) {
        engine {
            //use your Okhttp interceptor here
//            addInterceptor(interceptor)
        }
        install(JsonFeature) {
            serializer = GsonSerializer()
        }

    }.create(SimpleConfig.serverUrl, getRetrofitResponseTransformer())
}

/**
 * use your Retrofit Call Aapter
 */
inline fun getRetrofitResponseTransformer(): RequestResponseAdapter = object : RequestResponseAdapter {
    override suspend fun beforeBuildRequest(
        callInfo: ApiCallInfo,
        client: HttpClient
    ): ApiCallInfo {
        return callInfo
    }

    override suspend fun buildRequest(builder: HttpRequestBuilder, callInfo: ApiCallInfo) {

    }

    override suspend fun <OUT> handleException(
        e: Throwable,
        callInfo: ApiCallInfo,
        returnTypeInfo: TypeInfo
    ): OUT {
        throw e
    }

    override suspend fun <OUT> transformResponse(
        response: HttpResponse,
        callInfo: ApiCallInfo,
        returnTypeInfo: TypeInfo
    ): OUT {
        return if (returnTypeInfo.type == RetrofitResponseBody::class) {
            response.call.receive(returnTypeInfo) as OUT
        } else {
            (response.call.receive(typeInfo<RetrofitResponseBody<OUT>>()) as RetrofitResponseBody<OUT>).data
        }
    }
}