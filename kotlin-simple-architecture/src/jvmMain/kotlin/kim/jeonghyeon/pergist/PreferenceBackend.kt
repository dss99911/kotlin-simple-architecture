package kim.jeonghyeon.pergist

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kim.jeonghyeon.db.SimpleDB
import kim.jeonghyeon.extension.fromJsonString
import kim.jeonghyeon.extension.toJsonString
import kim.jeonghyeon.extension.toJsonStringNew
import kim.jeonghyeon.util.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

actual class Preference(path: String, properties: Map<String?, String?> = mapOf()) : AbstractPreference() {
    actual constructor() : this(IN_MEMORY, emptyMap())

    actual override val db: SimpleDB = SimpleDB(
        JdbcSqliteDriver(path, Properties().apply { properties.forEach { this.setProperty(it.key, it.value) } })
            .also {
                try {
                    SimpleDB.Schema.create(it)
                } catch (e: Exception) {
                    //if already created, ignore the message.
                    log.e(e.message)
                }
            }
    )

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

    actual companion object {
        const val IN_MEMORY = JdbcSqliteDriver.IN_MEMORY
    }
}