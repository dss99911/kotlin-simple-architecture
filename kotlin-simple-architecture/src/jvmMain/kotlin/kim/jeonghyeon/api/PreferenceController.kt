package kim.jeonghyeon.api

import kim.jeonghyeon.pergist.Preference

class PreferenceController(val preference: Preference) : PreferenceApi {
    override suspend fun getString(key: String): String? = preference.getString(key)

    override suspend fun setString(key: String, value: String?) {
        preference.setString(key, value)
    }
}