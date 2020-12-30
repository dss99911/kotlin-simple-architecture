package kim.jeonghyeon.sample.api

import kim.jeonghyeon.annotation.*
import kotlinx.serialization.Serializable

@Api
interface TestApi {
    suspend fun checkNull(text: String?): String?

    suspend fun checkEnum(enum: EnumValue?): EnumValue?

    suspend fun checkGeneric(genericType: GenericType<EnumValue>?): GenericType<EnumValue>?

    @Get("aa/{name}")
    suspend fun checkAnnotation(@Path("name") name: String?, @Query("queryName") queryName: String?, @Query("queryByObj") queryObj: GenericType<EnumValue>?, @Header("head") header: String?)

    suspend fun checkEmptyFunction()
}

enum class EnumValue {
    A, B
}

@Serializable
data class GenericType<T>(val text: T, val sub: GenericSubType<T>)

@Serializable
data class GenericSubType<T>(val type: T)