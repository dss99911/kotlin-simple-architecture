package kim.jeonghyeon.androidlibrary.architecture.net.adapter

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class DataCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
            returnType: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) {
            return null
        }

        val genericType = getParameterUpperBound(0, returnType as ParameterizedType)

        return DataCallAdapter<Any>(
            genericType
        )
    }
}