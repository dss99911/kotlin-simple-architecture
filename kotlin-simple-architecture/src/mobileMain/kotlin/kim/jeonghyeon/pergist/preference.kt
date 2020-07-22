package kim.jeonghyeon.pergist

import kim.jeonghyeon.db.SimpleDB
import kim.jeonghyeon.generated.db.dbSimple

actual class Preference actual constructor() : AbstractPreference() {
    actual override val db: SimpleDB = dbSimple()
}