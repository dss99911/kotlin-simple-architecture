package kim.jeonghyeon.backend.di

import androidLibrary.sample.samplebase.generated.db.db
import kim.jeonghyeon.pergist.Preference
import kim.jeonghyeon.sample.SampleDb
import kim.jeonghyeon.sample.UserQueries

val dbPath = "jdbc:sqlite:sample-server.db"

var serviceLocator: ServiceLocator = ServiceLocatorImpl()

interface ServiceLocator {
    val sampleDb: SampleDb
    val preference: Preference
    val userQueries: UserQueries
}

class ServiceLocatorImpl : ServiceLocator {
    override val sampleDb: SampleDb
        get() = db(dbPath)
    override val userQueries: UserQueries = sampleDb.userQueries

    override val preference: Preference = Preference(dbPath)
}

