package kim.jeonghyeon.backend.di

import androidLibrary.sample.samplebase.generated.db.db
import com.auth0.jwt.algorithms.Algorithm
import kim.jeonghyeon.db.SimpleDB
import kim.jeonghyeon.db.UserQueries
import kim.jeonghyeon.di.ServiceLocator
import kim.jeonghyeon.kotlinsimplearchitecture.generated.db.dbSimple
import kim.jeonghyeon.pergist.Preference
import kim.jeonghyeon.sample.SampleDb
import kim.jeonghyeon.sample.UserDetailQueries

val dbPath = "jdbc:sqlite:sample-server.db"

val serviceLocator: SampleServiceLocator = SampleServiceLocatorImpl()

interface SampleServiceLocator : ServiceLocator {
    val sampleDb: SampleDb
    val preference: Preference
    val jwtAlgorithm: Algorithm
    val userDetailQueries: UserDetailQueries
}

class SampleServiceLocatorImpl : SampleServiceLocator {
    override val sampleDb: SampleDb get() = db(dbPath)
    override val preference: Preference = Preference(dbPath)

    //todo remove secret from code. what is the best approach? key store?
    // requirements
    //  - should be encrypted in storage(someone can take secret from storage)
    //  - shouldn't be in code(someone can take secret from code or application jar file)
    override val jwtAlgorithm: Algorithm = Algorithm.HMAC256("secret")

    override val userQueries: UserQueries = dbSimple<SimpleDB>(dbPath).userQueries
    override val userDetailQueries: UserDetailQueries = db<SampleDb>(dbPath).userDetailQueries
}


