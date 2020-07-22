package kim.jeonghyeon.pergist

import com.squareup.sqldelight.runtime.coroutines.asFlow
import kim.jeonghyeon.db.SimpleDB
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

expect class Preference() : AbstractPreference {
    override val db: SimpleDB
}

abstract class AbstractPreference {
    abstract val db: SimpleDB
    private val queries by lazy { db.dictionaryQueries }

    fun has(key: String): Boolean {
        return getString(key) != null
    }

    fun getString(key: String): String? {
        return queries.get(key).executeAsOneOrNull()?.value
    }

    fun getString(key: String, defValue: String): String {
        return getString(key) ?: defValue
    }

    fun getStringFlow(key: String): Flow<String?> {
        return queries.get(key).asFlow().map { it.executeAsOneOrNull()?.value }
    }

    fun setString(key: String, value: String?) {
        queries.set(key, value)
    }
}