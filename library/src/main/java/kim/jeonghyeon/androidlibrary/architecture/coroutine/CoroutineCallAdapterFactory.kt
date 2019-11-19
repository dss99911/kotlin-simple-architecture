package kim.jeonghyeon.androidlibrary.architecture.coroutine

import kim.jeonghyeon.androidlibrary.architecture.net.error.ResourceError
import kim.jeonghyeon.androidlibrary.architecture.net.model.BaseResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class CoroutineCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
            returnType: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) {
            return null
        }

        val genericType = getParameterUpperBound(0, returnType as ParameterizedType)
        val genericClass = getRawType(genericType)
        if (genericClass == BaseResponseBody::class.java) {
            return null//ex) Call<ResponseBody<Data>>
        }

        val baseResponseBodyGenericType = object : ParameterizedType {
            override fun getRawType(): Type {
                return BaseResponseBody::class.java
            }

            override fun getOwnerType(): Type? {
                return null
            }

            override fun getActualTypeArguments(): Array<Type> {
                return arrayOf(genericType)
            }
        }

        return CoroutineCallAdapter<Any, BaseResponseBody<Any>>(baseResponseBodyGenericType)
    }
}

class ResourceException(val error: ResourceError): Exception()