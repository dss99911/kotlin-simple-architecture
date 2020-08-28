package kim.jeonghyeon.di

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.Application
import kim.jeonghyeon.db.UserQueries

/**
 * this should be initalized
 */

lateinit var application: Application
val logger get() = application.environment.log

internal lateinit var serviceLocator: ServiceLocator

interface ServiceLocator {
    val userQueries: UserQueries
}