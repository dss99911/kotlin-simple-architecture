package kim.jeonghyeon.pergist

import kim.jeonghyeon.db.SimpleDB
import kim.jeonghyeon.extension.fromJsonString
import kim.jeonghyeon.extension.toJsonString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinsimplearchitectureclient.generated.db.dbSimpleFramework

val preference: Preference by lazy { Preference() }

val Preference.Companion.KEY_USER_TOKEN get() = "simple-user-token"

fun Preference.getUserToken(): String? {
    return getEncryptedString(Preference.KEY_USER_TOKEN)
}
fun Preference.removeUserToken() {
    setEncryptedString(Preference.KEY_USER_TOKEN, null)
}

open class Preference(path: String = "SimpleDB.db", properties: Map<String?, String?> = emptyMap()) {

    val db: SimpleDB = dbSimpleFramework(path, properties)
    private val queries by lazy { db.preferenceForAllQueries }

    fun has(key: String): Boolean {
        return getString(key) != null
    }

    fun getString(key: String): String? {
        return queries.get(key).executeAsOneOrNull()?.value
    }

    fun getString(key: String, defValue: String): String {
        return getString(key) ?: defValue
    }

    inline fun <reified T : Any> get(key: String): T? {
        return getString(key)?.fromJsonString()
    }

    //todo issue : if Preference is created multiple times, and other preference change value. then this won't be invoked.
    // make this preference singleton or check sqldelight implementation
    fun getStringFlow(key: String): Flow<String?> {
        return queries.get(key).asFlow().map { it.executeAsOneOrNull()?.value }
    }

    fun setString(key: String, value: String?) {
        queries.set(key, value)
    }

    inline fun <reified T : Any> set(key: String, value: T?) {
        setString(key, value?.toJsonString())
    }

    //TODO HYUN [KSA-95] : add encyption logic
    fun getEncryptedString(key: String): String? {
        return getString(key)
    }

    //TODO HYUN [KSA-95] : add encyption logic
    fun setEncryptedString(key: String, value: String?) {
        setString(key, value)
    }

    companion object
}

