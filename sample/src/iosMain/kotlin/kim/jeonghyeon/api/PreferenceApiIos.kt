package kim.jeonghyeon.api

import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.type.CRFlow
import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.suspendToCRFlow

/**
 * todo remove this, and use suspend function directly on kotlin 1.4 is released. as 1.4 support suspend function
 */
class PreferenceApiIos() {
    val api: PreferenceApi = serviceLocator.preferenceApi

    fun getString(key: String): CRFlow<String?> = suspendToCRFlow { api.getString(key) }

    fun setString(key: String, value: String?): CRFlow<Unit> = suspendToCRFlow {
        api.setString(key, value)
    }
}

fun start(): Resource<*> = Resource.Start