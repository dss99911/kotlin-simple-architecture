package kim.jeonghyeon.backend.controller

import kim.jeonghyeon.sample.api.EnumValue
import kim.jeonghyeon.sample.api.GenericSubType
import kim.jeonghyeon.sample.api.GenericType
import kim.jeonghyeon.sample.api.TestApi

class ApiTestController : TestApi {
    override suspend fun checkNull(text: String?): String? {
        return text
    }

    override suspend fun checkEnum(enum: EnumValue?): EnumValue? {
        return enum
    }

    override suspend fun checkGeneric(genericType: GenericType<EnumValue>?): GenericType<EnumValue>? {
        return genericType
    }

    override suspend fun checkAnnotation(
        name: String?,
        queryName: String?,
        queryObj: GenericType<EnumValue>?,
        header: String?
    ) {

    }

    override suspend fun checkEmptyFunction() {

    }
}