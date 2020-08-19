package kim.jeonghyeon

import io.ktor.application.Application
import kim.jeonghyeon.di.ServiceLocator
import kim.jeonghyeon.di.application
import kim.jeonghyeon.di.serviceLocator

fun Application.onApplicationCreate(_serviceLocator: ServiceLocator) {
    serviceLocator = _serviceLocator
    application = this
}