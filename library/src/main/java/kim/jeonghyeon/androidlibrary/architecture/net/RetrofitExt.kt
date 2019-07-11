package kim.jeonghyeon.androidlibrary.architecture.net

import kim.jeonghyeon.androidlibrary.architecture.net.adapter.LiveDataCallAdapterFactory
import kim.jeonghyeon.androidlibrary.architecture.net.interceptor.BaseInterceptor
import kim.jeonghyeon.kotlinlibrary.extension.alsoIf
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import kotlin.coroutines.suspendCoroutine

inline fun <reified API> api(baseUrl: String): API {
    val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor()
                    .apply { level = HttpLoggingInterceptor.Level.BASIC })
            .addInterceptor(BaseInterceptor())
            .build()

    return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(API::class.java)
}

@Throws(IOException::class, HttpException::class, RuntimeException::class)
suspend fun <T> Call<T>.body(): T? = suspendCoroutine {
    execute()
            .alsoIf({ !it.isSuccessful }) { throw HttpException(it) }
            .body()
}