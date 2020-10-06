package kim.jeonghyeon.di

import io.ktor.application.*
import kim.jeonghyeon.db.UserQueries

/**
 * this should be initalized
 */

lateinit var application: Application
val logger get() = application.environment.log

private var _serviceLocator: ServiceLocator? = null
internal var serviceLocator: ServiceLocator
    get() = _serviceLocator ?: error("serviceLocator should be initialized on SimpleFeature")
    set(value) {
        _serviceLocator = value
    }

interface ServiceLocator {
    val userQueries: UserQueries
}