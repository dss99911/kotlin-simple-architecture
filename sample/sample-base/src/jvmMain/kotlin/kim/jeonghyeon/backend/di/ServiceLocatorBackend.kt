package kim.jeonghyeon.backend.di

import androidLibrary.sample.samplebase.generated.SimpleConfig
import androidLibrary.sample.samplebase.generated.db.db
import com.auth0.jwt.algorithms.Algorithm
import kim.jeonghyeon.db.SimpleDB
import kim.jeonghyeon.db.UserQueries
import kim.jeonghyeon.di.ServiceLocator
import kim.jeonghyeon.kotlinsimplearchitecture.generated.db.dbSimple
import kim.jeonghyeon.pergist.Preference
import kim.jeonghyeon.sample.SampleDb

val serviceLocator: ServiceLocatorBackend = ServiceLocatorBackendImpl()

interface ServiceLocatorBackend : ServiceLocator {
    val environment: Environment
    val sampleDb: SampleDb
    val preference: Preference
    val jwtAlgorithm: Algorithm
}

class ServiceLocatorBackendImpl : ServiceLocatorBackend {
    override val environment: Environment = makeEnvironment()

    override val sampleDb: SampleDb get() = db(environment.dbPath)
    override val preference: Preference by lazy { Preference(environment.dbPath) }

    //todo remove secret from code. what is the best approach? key store?
    // requirements
    //  - should be encrypted in storage(someone can take secret from storage)
    //  - shouldn't be in code(someone can take secret from code or application jar file)
    override val jwtAlgorithm: Algorithm by lazy { Algorithm.HMAC256("secret") }

    override val userQueries: UserQueries by lazy { dbSimple<SimpleDB>(environment.dbPath).userQueries }


}

//todo how to remove this configuration from source code especially for production environment
fun makeEnvironment(): Environment =
    if (SimpleConfig.isProduction)
        Environment(
            logPath = "/home/ec2-user/app/sample-backend",
            logLevel = "TRACE",
            dbPath = "jdbc:sqlite:/home/ec2-user/app/sample-backend/sample-server.db"
        )
    else
        Environment(
            logPath = ".",
            logLevel = "TRACE",
            dbPath = "jdbc:sqlite:sample-server.db"
        )

data class Environment(
    val logPath: String,
    val logLevel: String,
    val dbPath: String
)