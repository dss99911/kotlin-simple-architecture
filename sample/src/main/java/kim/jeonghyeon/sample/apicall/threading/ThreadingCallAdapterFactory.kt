package kim.jeonghyeon.androidlibrary.architecture.net.adapter

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type

class ThreadingCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) == Call::class.java) {
            return null
        }

        return ThreadingCallAdapter<Any>(
            getRawType(returnType)
        )
    }
}