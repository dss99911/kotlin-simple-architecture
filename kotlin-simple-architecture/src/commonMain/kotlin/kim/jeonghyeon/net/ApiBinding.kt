@file:UseSerializers(HttpMethodSerializer::class)

package kim.jeonghyeon.net

import io.ktor.client.*
import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.annotation.Authenticate
import kim.jeonghyeon.annotation.SimpleArchInternal
import kim.jeonghyeon.type.AtomicReference
import kim.jeonghyeon.type.atomic
import kotlinsimplearchitecture.generated.net.createSimple
import kotlinx.coroutines.withContext
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.reflect.KProperty0

class ApiCallInfoAndSerializer<T>(val apiCallInfo: ApiCallInfo, val serializer: KSerializer<T>)


/**
 * @param key if it's [ApiParameterType.BODY] type, there is no key
 * @param value if it's [ApiParameterType.BODY] type, it's Any?. other type is String.
 */
@Serializable
data class ApiParameterInfo(val type: ApiParameterType, val key: String?, @Contextual val value: Any?)

//todo delete after annotation library publish
enum class ApiParameterType {
    HEADER, QUERY, PATH, BODY
}

class ApiBindingStore : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*>
        get() = Key

    companion object Key : CoroutineContext.Key<ApiBindingStore>

    val parameterBinding: AtomicReference<MutableMap<Int, String>> = atomic(mutableMapOf())

    /**
     * bind parameter with previous api response
     *
     * @param responsePath : if it's response it self, it's response index, else, field name. ex) 0.data.text (first response contains `data` field, and `data` is object and contains `text` field)
     */
    fun putParameterBinding(index: Int, responsePath: String) {
        parameterBinding.value = mutableMapOf(*parameterBinding.value.toList().toTypedArray())
            .apply {
                put(index, responsePath)
            }
    }

}

@SimpleArchInternal
suspend fun isApiBinding(): Boolean = coroutineContext[ApiBindingStore] != null


class ApiBindingException(val apiCallInfo: ApiCallInfo) : RuntimeException()

suspend inline fun <reified DATA> getApiCallInfo(crossinline call: suspend () -> DATA): ApiCallInfoAndSerializer<DATA> {
    val apiBindingStore = ApiBindingStore()
    return ApiCallInfoAndSerializer(withContext(coroutineContext + apiBindingStore) {
        try {
            call()
            error("this shouldn't be reached")
        } catch (e: ApiBindingException) {
            e.apiCallInfo
        }
    }.copy(parameterBinding = apiBindingStore.parameterBinding.value), serializer())
}

suspend inline fun <reified DATA1> HttpClient.bindApi(noinline call: suspend ()-> DATA1): ApiBinder1<DATA1> {
    return ApiBinder1(this, getApiCallInfo(call))
}

class ApiBinder1<DATA1>(val client: HttpClient, val apiCallInfo: ApiCallInfoAndSerializer<DATA1>) {
    suspend inline fun <reified DATA2> bindApi(crossinline call: suspend (data1: ResponseBinder<DATA1>)-> DATA2): ApiBinder2<DATA1, DATA2> =
        ApiBinder2(client, apiCallInfo, getApiCallInfo {
            call(ResponseBinder(0, apiCallInfo.serializer))
        })
}

@Api
interface ApiBindingApi {
    suspend fun call(apiCallInfos: List<ApiCallInfo>): List<String>
    @Authenticate
    suspend fun callWithAuth(apiCallInfos: List<ApiCallInfo>): List<String>
}

class ApiBinder2<DATA1, DATA2>(val client: HttpClient, val apiCallInfo1: ApiCallInfoAndSerializer<DATA1>, val apiCallInfo2: ApiCallInfoAndSerializer<DATA2>) {

    suspend inline fun <reified DATA3> bindApi(crossinline call: (data1: ResponseBinder<DATA1>, data2: ResponseBinder<DATA2>)-> DATA3): ApiBinder3<DATA1, DATA2, DATA3> =
        ApiBinder3(client, apiCallInfo1, apiCallInfo2, getApiCallInfo { call(ResponseBinder(0, apiCallInfo1.serializer), ResponseBinder(1, apiCallInfo2.serializer)) })

    suspend fun execute(): Pair<DATA1, DATA2> {
        val api = client.createSimple<ApiBindingApi>(apiCallInfo1.apiCallInfo.baseUrl)
        val isAuthRequired = apiCallInfo1.apiCallInfo.isAuthRequired || apiCallInfo2.apiCallInfo.isAuthRequired

        val params = listOf(apiCallInfo1.apiCallInfo, apiCallInfo2.apiCallInfo)
        val result = if (isAuthRequired) {
            api.callWithAuth(params)
        } else {
            api.call(params)
        }

        val json = Json { ignoreUnknownKeys = true }
        //todo handle exception. if any call is failed return error with success data info.
        return Pair(
            json.decodeFromString(apiCallInfo1.serializer, result[0]),
            json.decodeFromString(apiCallInfo2.serializer, result[1])
        )

    }
}

class ApiBinder3<DATA1, DATA2, DATA3>(val client: HttpClient, val apiCallInfo1: ApiCallInfoAndSerializer<DATA1>, val apiCallInfo2: ApiCallInfoAndSerializer<DATA2>, val apiCallInfo3: ApiCallInfoAndSerializer<DATA3>) {
//    todo add more binder : suspend fun <DATA3> bindApi(call: (data1: ResponseBinder<DATA1>, data2: ResponseBinder<DATA2>)-> DATA3): ApiBinder3<DATA1, DATA2, DATA3> {
//
//    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun execute(): Triple<DATA1, DATA2, DATA3> {
        val result = client
            .createSimple<ApiBindingApi>(apiCallInfo1.apiCallInfo.baseUrl)
            .call(listOf(apiCallInfo1.apiCallInfo, apiCallInfo2.apiCallInfo, apiCallInfo3.apiCallInfo))

        val json = Json { ignoreUnknownKeys = true }
        //todo handle exception. if any call is failed return error with success data info.
        return Triple(
            json.decodeFromString(apiCallInfo1.serializer, result[0]),
            json.decodeFromString(apiCallInfo2.serializer, result[0]),
            json.decodeFromString(apiCallInfo3.serializer, result[1])
        )
    }

}

class ResponseBinder<T>(val responseIndex: Int, val serializer: KSerializer<T>) {

    /**
     * when setting response as parameter
     */
    suspend fun bindParameter(parameterIndex: Int): T {
        coroutineContext[ApiBindingStore]!!.putParameterBinding(parameterIndex, "$responseIndex")
        return serializer.createEmpty()
    }

    /**
     * when setting response's field as parameter
     */
    suspend inline fun <reified U> bindParameter(parameterIndex: Int, response: PropertyBinder.(response: T) -> KProperty0<U>): U {
        val apiBindingStore = coroutineContext[ApiBindingStore]!!

        apiBindingStore.putParameterBinding(parameterIndex, "$responseIndex")

        val lastProperty = PropertyBinder(parameterIndex).response(serializer.createEmpty())

        val responsePath = apiBindingStore.parameterBinding.value[parameterIndex]!!

        apiBindingStore.putParameterBinding(parameterIndex, responsePath + "." + lastProperty.name)
        return serializer<U>().createEmpty()
    }
}

class PropertyBinder(val parameterIndex: Int) {

    suspend inline fun <reified U> KProperty0<U>.bind(): U {
        val apiBindingStore = coroutineContext[ApiBindingStore]!!

        val responsePath = apiBindingStore.parameterBinding.value[parameterIndex]!!

        apiBindingStore.putParameterBinding(parameterIndex, "$responsePath.$name")
        return serializer<U>().createEmpty()
    }
}

fun <T> DeserializationStrategy<T>.createEmpty(): T =
    deserialize(EmptyDecoder())

private class EmptyDecoder : AbstractDecoder() {
    var indexMap = mutableMapOf<SerialDescriptor, Int>()
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return this
    }


    override fun decodeBoolean(): Boolean {
        return false
    }

    override fun decodeByte(): Byte {
        return 0
    }

    override fun decodeChar(): Char {
        return ' '
    }

    override fun decodeDouble(): Double {
        return 0.0
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        return 0
    }

    override fun decodeFloat(): Float {
        return 0f
    }

    override fun decodeInt(): Int {
        return 0
    }

    override fun decodeLong(): Long {
        return 0
    }

    @ExperimentalSerializationApi
    override fun decodeNotNullMark(): Boolean {
        return false
    }

    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing? {
        return null
    }

    override fun decodeShort(): Short {
        return 0
    }

    override fun decodeString(): String {
        return ""
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        val index = indexMap.getOrPut(descriptor) { 0 }

        return if (index < descriptor.elementsCount) {
            indexMap.put(descriptor, index + 1)
            index
        } else {
            CompositeDecoder.DECODE_DONE
        }
    }
}