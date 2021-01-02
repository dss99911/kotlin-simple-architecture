package kim.jeonghyeon.sample.api

import kim.jeonghyeon.util.log
import okio.Timeout
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

fun getRetrofitAdapterFactory(): CallAdapter.Factory {
    return object : CallAdapter.Factory() {
        override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
            val callType = getParameterUpperBound(0, returnType as ParameterizedType)
            return if (Data::class.java.isAssignableFrom(getRawType(callType))) {
                val responseGenericType = object : ParameterizedType {
                    override fun getRawType(): Type = RetrofitResponseBody::class.java
                    override fun getOwnerType(): Type? = null
                    override fun getActualTypeArguments(): Array<Type> = arrayOf(callType)
                }
                RetrofitAdapter<Data, RetrofitResponseBody<Data>>(responseGenericType)
            } else {
                null
            }
        }
    }
}

class RetrofitAdapter<U : Data, T : RetrofitResponseBody<U>>(private val type: Type)
    : CallAdapter<T, Call<U>> {
    override fun responseType() = type
    override fun adapt(call: Call<T>): Call<U> = RetrofitDelegate(call)

    class RetrofitDelegate<U : Data, T : RetrofitResponseBody<U>>(proxy: Call<T>) :
            CallDelegate<T, U>(proxy) {
        override fun enqueueImpl(callback: Callback<U>) {
            proxy.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.body() == null) {
                        onFail(null, callback)
                        return
                    }

                    if (!response.isSuccessful) {
                        onFail(null, callback)
                        return
                    }

                    val body: T? = response.body()
                    if (body == null) {
                        onFail(null, callback)
                        return
                    }

                    body.data.let {
                        onSuccess(body, callback)
                    }

                    onFail(body, callback)
                }

                override fun onFailure(call: Call<T>, t: Throwable) = onFail(null, callback)
            })
        }

        override fun cloneImpl(): Call<U> = RetrofitDelegate(proxy.clone())
        
        fun onSuccess(response: T, callback: Callback<U>) =
                callback.onResponse(this@RetrofitDelegate, Response.success(response.data))

        fun onFail(response: T?, callback: Callback<U>) =
                callback.onFailure(this@RetrofitDelegate, RuntimeException("some error"))

        override fun timeout(): Timeout = proxy.timeout()
    }

    abstract class CallDelegate<TIn, TOut>(
            protected val proxy: Call<TIn>
    ) : Call<TOut> {
        final override fun execute(): Response<TOut> = throw NotImplementedError()

        final override fun enqueue(callback: Callback<TOut>) = enqueueImpl(callback)
        final override fun clone(): Call<TOut> = cloneImpl()

        override fun cancel() = proxy.cancel()
        override fun request(): okhttp3.Request = proxy.request()
        override fun isExecuted() = proxy.isExecuted
        override fun isCanceled() = proxy.isCanceled

        abstract fun enqueueImpl(callback: Callback<TOut>)
        abstract fun cloneImpl(): Call<TOut>
    }
}
