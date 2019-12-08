package kim.jeonghyeon.androidlibrary.architecture.livedata

import androidx.lifecycle.LiveData
import kim.jeonghyeon.androidlibrary.architecture.net.model.BaseResponseBody
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class LiveDataCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
            returnType: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != LiveData::class.java) {
            return null
        }

        val resourceType = getParameterUpperBound(0, returnType as ParameterizedType)
        val resourceClass = getRawType(resourceType)
        require(resourceClass == Resource::class.java) { "type must be a resource" }
        require(resourceType is ParameterizedType) { "resource must be parameterized" }
        val resourceGenericType = getParameterUpperBound(0, resourceType)

        val baseResponseBodyGenericType = object : ParameterizedType {
            override fun getRawType(): Type {
                return BaseResponseBody::class.java
            }

            override fun getOwnerType(): Type? {
                return null
            }

            override fun getActualTypeArguments(): Array<Type> {
                return arrayOf(resourceGenericType)
            }
        }
        return LiveDataCallAdapter<Any, BaseResponseBody<Any>>(
            baseResponseBodyGenericType
        )
    }
}
