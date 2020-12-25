package kim.jeonghyeon.pergist

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kim.jeonghyeon.db.SimpleDB
import kim.jeonghyeon.extension.fromJsonString
import kim.jeonghyeon.extension.toJsonString
import kim.jeonghyeon.util.log
import kotlinsimpleapiclient.generated.db.dbSimple
import java.util.*

class UserPreference(path: String = "SimpleDB.db", properties: Map<String?, String?> = emptyMap()) : Preference(path, properties) {

    private val perUserQueries by lazy { db.preferencePerUserQueries }

    fun hasPerUser(key: String, id: Long): Boolean {
        return getStringPerUser(key, id) != null
    }

    fun getStringPerUser(key: String, id: Long): String? {
        return perUserQueries.get(key, id).executeAsOneOrNull()?.value
    }

    fun getStringPerUser(key: String, id: Long, defValue: String): String {
        return getStringPerUser(key, id) ?: defValue
    }

    inline fun <reified T : Any> getPerUser(key: String, id: Long): T? {
        return getStringPerUser(key, id)?.fromJsonString()
    }

    fun setStringPerUser(key: String, id: Long, value: String?) {
        perUserQueries.set(key, id, value)
    }

    inline fun <reified T : Any> setPerUser(key: String, id: Long, value: T?) {
        setStringPerUser(key, id, value?.toJsonString())
    }

    //TODO HYUN [KSA-95] : add encyption logic
    fun getEncryptedString(key: String, id: Long): String? {
        return getStringPerUser(key, id)
    }

    //TODO HYUN [KSA-95] : add encyption logic
    fun setEncryptedString(key: String, id: Long, value: String?) {
        setStringPerUser(key, id, value)
    }
}