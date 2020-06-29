package kim.jeonghyeon.pergist

import kotlinx.coroutines.flow.Flow

expect class Preference {
    fun has(key: String): Boolean

    fun getString(key: String): String?
    fun getString(key: String, defValue: String): String
    fun getStringFlow(key: String): Flow<String?>

    fun setString(key: String, value: String?)
}