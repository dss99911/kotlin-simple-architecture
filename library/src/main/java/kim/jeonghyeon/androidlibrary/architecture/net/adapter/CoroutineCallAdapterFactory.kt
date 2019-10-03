//package kim.jeonghyeon.androidlibrary.architecture.net.adapter
//
//import com.google.android.material.snackbar.BaseTransientBottomBar
//import kim.jeonghyeon.androidlibrary.architecture.net.model.BaseResponseBody
//import okhttp3.Request
//import retrofit2.*
//import java.io.IOException
//import java.lang.reflect.ParameterizedType
//import java.lang.reflect.Type
//
//class CoroutineCallAdapterFactory private constructor() : CallAdapter.Factory() {
//    companion object {
//        @JvmStatic
//        @JvmName("create")
//        operator fun invoke() = CoroutineCallAdapterFactory()
//    }
//
//    class ResultAdapter<U, T : BaseResponseBody<U>>(
//            private val type: Type
//    ) : CallAdapter<T, Call<U>> {
//        override fun responseType() = type
//        override fun adapt(call: Call<T>): Call<U> = ResultCall(call)
//
//        abstract class CallDelegate<TIn, TOut>(
//                protected val proxy: Call<TIn>
//        ) : Call<TOut> {
//            final override fun execute(): Response<TOut> = throw NotImplementedError()
//
//
//            final override fun enqueue(callback: Callback<TOut>) = enqueueImpl(callback)
//            final override fun clone(): Call<TOut> = cloneImpl()
//
//            override fun cancel() = proxy.cancel()
//            override fun request(): Request = proxy.request()
//            override fun isExecuted() = proxy.isExecuted
//            override fun isCanceled() = proxy.isCanceled
//
//            abstract fun enqueueImpl(callback: Callback<TOut>)
//            abstract fun cloneImpl(): Call<TOut>
//        }
//
//        class ResultCall<U, T : BaseResponseBody<U>>(proxy: Call<T>) : CallDelegate<T, U>(proxy) {
//            override fun enqueueImpl(callback: Callback<U>) {
//                try {
//                    val execute = proxy.execute()
//                } catch (e: Exception) {
//
//                }
//
//
//                val baseCallback =
//                    BaseTransientBottomBar.BaseCallback(object : BaseResponseListenerV2<T>() {
//                        override fun onSuccess(response: T) {
//                            callback.onResponse(this@ResultCall, Response.success(response.data))
//                        }
//
//                        override fun onFail(response: T?) {
//                            throw ResultException(Result.Failure(response?.data?.code))
//                        }
//
//                        override fun onError(throwable: Throwable?) {
//                            throw ResultException(throwable?.toResult() ?: Result.Error("Unknown"))
//                        }
//
//                        override fun showAlert(alert: Alert) {
//                            throw ResultException(Result.ShowAlert(alert))
//                        }
//                    })
//                try {
//                    baseCallback.onResponse(proxy, proxy.execute())
//                } catch (ex: Exception) {
//                    baseCallback.onFailure(proxy, ex)
//                }
//            }
//
//            override fun cloneImpl(): Call<U> {
//                return ResultCall(proxy.clone())
//            }
//
//            private fun Throwable.toResult(): Result<Nothing> = when (this) {
//                is IOException -> {
//                    Result.NetworkError
//                }
//                else -> {
//                    Result.Error(message ?: "Unknown")
//                }
//            }
//
//        }
//    }
//
//
//
//    override fun get(
//            returnType: Type,
//            annotations: Array<Annotation>,
//            retrofit: Retrofit
//    ) = when (getRawType(returnType)) {
//        Call::class.java -> {
//            val callType = getParameterUpperBound(0, returnType as ParameterizedType)
//
//            if (BaseData::class.java.isAssignableFrom(getRawType(callType))) {
//                val baseResponseV2GenericType = object : ParameterizedType {
//                    override fun getRawType(): Type {
//                        return BaseResponseV2::class.java
//                    }
//
//                    override fun getOwnerType(): Type? {
//                        return null
//                    }
//
//                    override fun getActualTypeArguments(): Array<Type> {
//                        return arrayOf(callType)
//                    }
//                }
//                ResultAdapter<BaseData, BaseResponseV2<BaseData>>(baseResponseV2GenericType)
//            } else null
//        }
//        else -> null
//    }
//}
//
//class ResultException(val result: Result<Nothing>): Exception()