package kim.jeonghyeon.androidlibrary.deprecated

import kim.jeonghyeon.kotlinlibrary.extension.alsoIf
import retrofit2.Call
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.suspendCoroutine

@Throws(IOException::class, HttpException::class, RuntimeException::class)
suspend fun <T> Call<T>.body(): T? = suspendCoroutine {
    execute()
        .alsoIf({ !it.isSuccessful }) { throw HttpException(it) }
        .body()
}