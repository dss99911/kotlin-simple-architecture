package kim.jeonghyeon.api

import kim.jeonghyeon.net.ControllerUtil.userId
import kim.jeonghyeon.pergist.Preference

class PreferenceController(val preference: Preference) : PreferenceApi {
    override suspend fun getStringPerUser(key: String): String? =
        preference.getStringPerUser(key, userId())

    override suspend fun setStringPerUser(key: String, value: String?) {
        preference.setStringPerUser(key, userId(), value)
    }

    override suspend fun getString(key: String): String? = preference.getString(key)
}