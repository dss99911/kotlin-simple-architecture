package kim.jeonghyeon.pergist

import kotlinx.coroutines.flow.Flow

interface Preference {
    fun has(key: String): String

    fun <T> get(key: String): T?
    fun <T> get(key: String, defValue: T): T
    fun <T> getFlow(key: String): Flow<T>

    fun getString(key: String): String?
    fun getString(key: String, defValue: String): String
    fun getStringFlow(key: String): Flow<String>

    fun <T> set(key: String, value: T)
    fun setString(key: String, value: String)


}

expect val preference: Preference