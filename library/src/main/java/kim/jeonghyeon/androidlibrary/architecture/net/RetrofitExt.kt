package kim.jeonghyeon.androidlibrary.architecture.net

import kim.jeonghyeon.androidlibrary.architecture.net.adapter.DataCallAdapterFactory
import kim.jeonghyeon.androidlibrary.architecture.net.interceptor.BaseInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
            .addCallAdapterFactory(DataCallAdapterFactory())
            .build()
            .create(API::class.java)
}

