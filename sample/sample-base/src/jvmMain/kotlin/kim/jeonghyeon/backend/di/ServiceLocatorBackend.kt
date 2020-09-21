package kim.jeonghyeon.backend.di

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import kim.jeonghyeon.backend.dbPath
import kim.jeonghyeon.backend.jwtSecret
import kim.jeonghyeon.db.SimpleDB
import kim.jeonghyeon.db.UserQueries
import kim.jeonghyeon.di.ServiceLocator
import kim.jeonghyeon.pergist.Preference
import kim.jeonghyeon.sample.SampleDb
import kotlinsimplearchitecture.generated.db.dbSimple
import samplebase.generated.db.db

lateinit var serviceLocatorBackend: ServiceLocatorBackend


interface ServiceLocatorBackend : ServiceLocator {
    val sampleDb: SampleDb
    val preference: Preference
    val jwtAlgorithm: Algorithm
}

class ServiceLocatorBackendImpl(val application: Application) : ServiceLocatorBackend {

    override val sampleDb: SampleDb get() = db(application.dbPath)
    override val preference: Preference by lazy { Preference(application.dbPath) }

    override val jwtAlgorithm: Algorithm by lazy { Algorithm.HMAC256(application.jwtSecret) }

    override val userQueries: UserQueries by lazy { dbSimple<SimpleDB>(application.dbPath).userQueries }
}