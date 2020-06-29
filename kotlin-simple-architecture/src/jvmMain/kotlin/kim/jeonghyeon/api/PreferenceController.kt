package kim.jeonghyeon.api

import kim.jeonghyeon.pergist.Preference

val preference by lazy { Preference() }

class PreferenceController : PreferenceApi {
    override suspend fun getString(key: String): String? = preference.getString(key)

    override suspend fun setString(key: String, value: String?) {
        preference.setString(key, value)
    }
}