package kim.jeonghyeon.pergist

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kim.jeonghyeon.db.SimpleDB
import kotlinx.coroutines.flow.Flow
import java.util.*

actual class Preference(path: String = IN_MEMORY, properties: Map<String?, String?> = emptyMap()) {
    private val driver = JdbcSqliteDriver(path, Properties().apply { properties.forEach { this.setProperty(it.key, it.value) } })
    private val queries = SimpleDB(driver).dictionaryQueries

    actual fun has(key: String): Boolean {
        return getString(key) != null
    }

    actual fun getString(key: String): String? {
        return queries.get(key).executeAsOneOrNull()?.value
    }

    actual fun getString(key: String, defValue: String): String {
        return getString(key) ?: defValue
    }

    actual fun getStringFlow(key: String): Flow<String?> {
        return queries.get(key).asFlow().map { it.executeAsOneOrNull()?.value }
    }

    actual fun setString(key: String, value: String?) {
        queries.set(key, value)
    }

    companion object {
        const val IN_MEMORY = JdbcSqliteDriver.IN_MEMORY
    }
}