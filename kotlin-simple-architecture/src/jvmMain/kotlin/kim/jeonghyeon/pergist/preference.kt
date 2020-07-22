package kim.jeonghyeon.pergist

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kim.jeonghyeon.db.SimpleDB
import java.util.*

actual class Preference(path: String, properties: Map<String?, String?>) : AbstractPreference() {
    actual constructor() : this(IN_MEMORY, emptyMap())

    actual override val db: SimpleDB = SimpleDB(
        JdbcSqliteDriver(path, Properties().apply { properties.forEach { this.setProperty(it.key, it.value) } })
            .also {
                //todo if it's not inmemory. is this required?
                SimpleDB.Schema.create(it)
            }
    )

    companion object {
        const val IN_MEMORY = JdbcSqliteDriver.IN_MEMORY
    }
}