package kim.jeonghyeon.androidlibrary.architecture.coroutine

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kim.jeonghyeon.androidlibrary.architecture.net.error.*
import kim.jeonghyeon.androidlibrary.architecture.net.model.BaseResponseBody
import kim.jeonghyeon.androidlibrary.architecture.net.model.ErrorBody
import okhttp3.Request
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type

class DataCallAdapter<U, T : BaseResponseBody<U>>(
    private val type: Type
) : CallAdapter<T, Call<U>> {
    override fun responseType() = type
    override fun adapt(call: Call<T>): Call<U> = ResourceCall(call)

    abstract class CallDelegate<TIn, TOut>(
        protected val proxy: Call<TIn>
    ) : Call<TOut> {
        final override fun execute(): Response<TOut> = throw NotImplementedError()


        final override fun enqueue(callback: Callback<TOut>) = enqueueImpl(callback)
        final override fun clone(): Call<TOut> = cloneImpl()

        override fun cancel() = proxy.cancel()
        override fun request(): Request = proxy.request()
        override fun isExecuted() = proxy.isExecuted
        override fun isCanceled() = proxy.isCanceled

        abstract fun enqueueImpl(callback: Callback<TOut>)
        abstract fun cloneImpl(): Call<TOut>
    }

    class ResourceCall<U, T : BaseResponseBody<U>>(proxy: Call<T>) : CallDelegate<T, U>(proxy) {
        override fun enqueueImpl(callback: Callback<U>) {
            proxy.enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    val resourceError = when (t) {
                        is IOException -> NoNetworkError(t)
                        else -> UnknownError(t)
                    }
                    callback.onFailure(this@ResourceCall, resourceError)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    onResponse(response, callback)
                }
            })
        }

        private fun onResponse(response: Response<T>, callback: Callback<U>) {
            val body = response.body()
            throw if (response.isSuccessful && body != null) {
                if (body.isSuccess()) {
                    callback.onResponse(this, Response.success(body.data))
                    return
                } else {
                    MessageCodeError(body.code, body.message?:"")
                }
            } else {
                val errorBodyString = response.errorBody()?.string()
                if (!errorBodyString.isNullOrEmpty()) {
                    try {
                        val errorBody = Gson().fromJson(errorBodyString, ErrorBody::class.java)
                        ErrorBodyError(errorBody)
                    } catch (e: JsonSyntaxException) {
                        e.printStackTrace()
                        getHttpError(response)
                    }
                } else {
                    getHttpError(response)
                }
            }
        }

        private fun getHttpError(response: Response<T>): ResourceError {
            val msg = response.message()

            return if (msg.isNullOrEmpty()) {
                UnknownError()
            } else {
                HttpError(response.code(), msg)
            }
        }

        override fun cloneImpl(): Call<U> {
            return ResourceCall(proxy.clone())
        }

    }
}
