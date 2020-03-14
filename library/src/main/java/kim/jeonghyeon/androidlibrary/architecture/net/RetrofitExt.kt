package kim.jeonghyeon.androidlibrary.architecture.net

import kim.jeonghyeon.androidlibrary.architecture.net.adapter.DataCallAdapterFactory
import kim.jeonghyeon.androidlibrary.architecture.net.interceptor.BaseInterceptor
import kim.jeonghyeon.androidlibrary.extension.isProdRelease
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

inline fun <reified API> api(baseUrl: String): API {
    return apiBuilder(baseUrl)
        .build()
        .create(API::class.java)
}

fun apiBuilder(baseUrl: String): Retrofit.Builder {
    val client = OkHttpClient.Builder().apply {
        if (!isProdRelease) {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
        addInterceptor(BaseInterceptor())
    }.build()

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(DataCallAdapterFactory())
}