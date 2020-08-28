package kim.jeonghyeon.pergist

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kim.jeonghyeon.db.SimpleDB
import kim.jeonghyeon.util.log
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

    companion object {
        const val IN_MEMORY = JdbcSqliteDriver.IN_MEMORY
    }
}