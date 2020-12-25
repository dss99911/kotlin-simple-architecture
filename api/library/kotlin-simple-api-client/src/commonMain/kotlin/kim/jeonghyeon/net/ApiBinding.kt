@file:UseSerializers(HttpMethodSerializer::class)

package kim.jeonghyeon.net

import io.ktor.client.*
import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.annotation.Authenticate
import kim.jeonghyeon.annotation.SimpleArchInternal
import kim.jeonghyeon.type.AtomicReference
import kim.jeonghyeon.type.atomic
import kotlinsimpleapiclient.generated.net.createSimple
import kotlinx.coroutines.withContext
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.reflect.KProperty0

class ApiCallInfoAndSerializer<T>(val apiCallInfo: ApiCallInfo, val serializer: KSerializer<T>, val client: HttpClient)


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


class ApiBindingException(val apiCallInfo: ApiCallInfo, val client: HttpClient) : RuntimeException()

suspend inline fun <reified DATA> getApiCallInfo(crossinline call: suspend () -> DATA): ApiCallInfoAndSerializer<DATA> {
    val apiBindingStore = ApiBindingStore()
    val exception = withContext(coroutineContext + apiBindingStore) {
        try {
            call()
            error("this shouldn't be reached")
        } catch (e: ApiBindingException) {
            e
        }
    }
    return ApiCallInfoAndSerializer(exception.apiCallInfo.copy(parameterBinding = apiBindingStore.parameterBinding.value), serializer(), exception.client)
}

suspend inline fun <reified DATA1> bindApi(noinline call: suspend ()-> DATA1): ApiBinder1<DATA1> {
    return ApiBinder1(getApiCallInfo(call))
}

class ApiBinder1<DATA1>(val apiCallInfo: ApiCallInfoAndSerializer<DATA1>) {
    suspend inline fun <reified DATA2> bindApi(crossinline call: suspend (data1: ResponseBinder<DATA1>)-> DATA2): ApiBinder2<DATA1, DATA2> =
        ApiBinder2(apiCallInfo.client, apiCallInfo, getApiCallInfo {
            call(ResponseBinder(0, apiCallInfo.serializer))
        })
}

@Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")
@Api
interface ApiBindingApi {
    suspend fun call(apiCallInfos: List<ApiCallInfo>): List<String>
    @Authenticate
    suspend fun callWithAuth(apiCallInfos: List<ApiCallInfo>): List<String>
}

abstract class ApiBinder {
    abstract val apiCallInfos: List<ApiCallInfoAndSerializer<*>>
    abstract val client: HttpClient

    suspend fun call(): List<Any?> {
        verifyCallInfos()

        val api = client.createSimple<ApiBindingApi>(apiCallInfos[0].apiCallInfo.baseUrl)

        val isAuthRequired = apiCallInfos.any { it.apiCallInfo.isAuthRequired }
        val result = if (isAuthRequired) {
            api.callWithAuth(apiCallInfos.map { it.apiCallInfo })
        } else {
            api.call(apiCallInfos.map { it.apiCallInfo })
        }

        val json = Json { ignoreUnknownKeys = true }
        return result.mapIndexed { index, s ->
            json.decodeFromString(apiCallInfos[index].serializer, s)
        }
    }

    private fun verifyCallInfos() {
        val baseUrls = apiCallInfos.map { it.apiCallInfo.baseUrl }.toSet()
        if (baseUrls.size > 1) {
            error("[Api Binding] There are multiple base urls : $baseUrls")
        }
    }
}

class ApiBinder2<DATA1, DATA2>(override val client: HttpClient, val apiCallInfo1: ApiCallInfoAndSerializer<DATA1>, val apiCallInfo2: ApiCallInfoAndSerializer<DATA2>) : ApiBinder() {
    override val apiCallInfos: List<ApiCallInfoAndSerializer<*>> = listOf(apiCallInfo1, apiCallInfo2)

    suspend inline fun <reified DATA3> bindApi(crossinline call: suspend (data1: ResponseBinder<DATA1>, data2: ResponseBinder<DATA2>)-> DATA3): ApiBinder3<DATA1, DATA2, DATA3> =
        ApiBinder3(client, apiCallInfo1, apiCallInfo2, getApiCallInfo { call(ResponseBinder(0, apiCallInfo1.serializer), ResponseBinder(1, apiCallInfo2.serializer)) })

    suspend fun execute(): Pair<DATA1, DATA2> {
        val result = call()

        //todo handle exception. if any call is failed return error with success data info.
        // currently, no success data is returned.
        // after receive success data, call only failed api.
        @Suppress("UNCHECKED_CAST")
        return Pair(result[0] as DATA1, result[1] as DATA2)
    }
}

class ApiBinder3<DATA1, DATA2, DATA3>(override val client: HttpClient, val apiCallInfo1: ApiCallInfoAndSerializer<DATA1>, val apiCallInfo2: ApiCallInfoAndSerializer<DATA2>, val apiCallInfo3: ApiCallInfoAndSerializer<DATA3>): ApiBinder() {
    override val apiCallInfos: List<ApiCallInfoAndSerializer<*>> = listOf(apiCallInfo1, apiCallInfo2, apiCallInfo3)

    //    todo add more binder : suspend fun <DATA3> bindApi(call: (data1: ResponseBinder<DATA1>, data2: ResponseBinder<DATA2>)-> DATA3): ApiBinder3<DATA1, DATA2, DATA3> {
//
//    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun execute(): Triple<DATA1, DATA2, DATA3> {
        val result = call()

        //todo handle exception. if any call is failed return error with success data info.
        // currently, no success data is returned.
        // after receive success data, call only failed api.
        @Suppress("UNCHECKED_CAST")
        return Triple(
            result[0] as DATA1,
            result[1] as DATA2,
            result[2] as DATA3
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

@OptIn(ExperimentalSerializationApi::class)
private class EmptyDecoder : AbstractDecoder() {
    override val serializersModule: SerializersModule = EmptySerializersModule
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

private fun verifyCallInfos(vararg apiCallInfos: ApiCallInfo) {
    val baseUrls = apiCallInfos.map { it.baseUrl }.toSet()
    if (baseUrls.size > 1) {
        error("[Api Binding] There are multiple base urls : $baseUrls")
    }
}