package kim.jeonghyeon

import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.application.install
import io.ktor.util.AttributeKey
import kim.jeonghyeon.auth.*
import kim.jeonghyeon.di.ServiceLocator
import kim.jeonghyeon.di.application
import kim.jeonghyeon.di.serviceLocator
import kim.jeonghyeon.net.SimpleRouting

class SimpleFeature {
    class Configuration {
        var serviceLocator: ServiceLocator? = null
        internal var signConfig: (SignFeature.Configuration.() -> Unit)? = null
        internal var routingConfig: (SimpleRouting.Configuration.() -> Unit)? = null

        fun sign(config: SignFeature.Configuration.() -> Unit) {
            signConfig = config
        }

        fun routing(config: SimpleRouting.Configuration.() -> Unit) {
            routingConfig = config
        }
    }

    companion object Feature :
        ApplicationFeature<Application, Configuration, SimpleFeature> {
        override val key: AttributeKey<SimpleFeature> = AttributeKey("SimpleFeature")

        override fun install(
            pipeline: Application,
            configure: Configuration.() -> Unit
        ): SimpleFeature {
            val config = Configuration().apply(configure)

            pipeline.onApplicationCreate(config.serviceLocator)

            config.signConfig?.let {
                pipeline.install(SignFeature, it)
            }

            config.routingConfig?.let {
                pipeline.install(SimpleRouting, it)
            }

            return SimpleFeature()
        }
    }
}


fun Application.onApplicationCreate(_serviceLocator: ServiceLocator?) {
    _serviceLocator?.let {
        serviceLocator = it
    }
    application = this
}