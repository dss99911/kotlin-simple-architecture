package kim.jeonghyeon.sample.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kim.jeonghyeon.net.SimpleApiCustom
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import samplebase.generated.SimpleConfig
import samplebase.generated.net.create


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

data class RetrofitData(val name: String, val id: String) : Data

interface Data

fun getRetrofitApi(): RetrofitApi {
    val okHttpClient = OkHttpClient()
    val client = okHttpClient.newBuilder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(SimpleConfig.serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(getRetrofitAdapterFactory())
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

        install(SimpleApiCustom) {
            adapter = getSimpleApiAdapter()
        }
    }.create(SimpleConfig.serverUrl)
}

