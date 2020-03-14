package kim.jeonghyeon.androidlibrary.architecture.net.adapter

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kim.jeonghyeon.androidlibrary.architecture.net.error.*
import kim.jeonghyeon.androidlibrary.architecture.net.model.ErrorBody
import kim.jeonghyeon.androidlibrary.architecture.net.model.ResponseCodeConstants.ERROR_CUSTOM
import okhttp3.Request
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type

class DataCallAdapter<U>(
    private val type: Type
) : CallAdapter<U, Call<U>> {
    override fun responseType() = type
    override fun adapt(call: Call<U>): Call<U> = DataCall(type, call)

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

    class DataCall<U>(val type: Type, proxy: Call<U>) :
        CallDelegate<U, U>(proxy) {
        override fun enqueueImpl(callback: Callback<U>) {
            proxy.enqueue(object : Callback<U> {
                override fun onFailure(call: Call<U>, t: Throwable) {
                    val resourceError = when (t) {
                        is IOException -> NoNetworkError(t)
                        else -> UnknownResourceError(t)
                    }
                    callback.onFailure(this@DataCall, resourceError)
                }

                override fun onResponse(call: Call<U>, response: Response<U>) {
                    onResponse(response, callback)
                }
            })
        }

        private fun onResponse(response: Response<U>, callback: Callback<U>) {
            val body = response.body()
            if (response.isSuccessful) {
                callback.onResponse(this, Response.success(body?.convertUnit() as U))
            } else {
                val errorBodyString = response.errorBody()?.string()
                if (response.code() == ERROR_CUSTOM && !errorBodyString.isNullOrEmpty()) {
                    try {
                        ErrorBodyError(Gson().fromJson(errorBodyString, ErrorBody::class.java))
                    } catch (e: JsonSyntaxException) {
                        e.printStackTrace()
                        getHttpError(response)
                    }
                } else {
                    getHttpError(response)
                }.let {
                    callback.onFailure(this, it)
                }
            }
        }

        /**
         * if return type is omit, then return type is Unit. but server may return null or other value. in that case, we ignore the value and return just Unit
         */
        private fun U.convertUnit(): U {
            val returnType = type
            //TODO : is equal okay?
            return if (returnType == Unit.javaClass) {
                @Suppress("UNCHECKED_CAST")
                Unit as U
            } else this
        }

        private fun getHttpError(response: Response<U>): ResourceError {
            val msg = response.message()

            return if (msg.isNullOrEmpty()) {
                UnknownResourceError()
            } else {
                HttpError(response.code(), msg)
            }
        }

        override fun cloneImpl(): Call<U> {
            return DataCall(type, proxy.clone())
        }

    }
}
