package kim.jeonghyeon

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import io.ktor.util.AttributeKey
import kim.jeonghyeon.annotation.SimpleArchInternal
import kim.jeonghyeon.api.ApiBindingController
import kim.jeonghyeon.auth.*
import kim.jeonghyeon.di.ServiceLocator
import kim.jeonghyeon.di.application
import kim.jeonghyeon.di.serviceLocator
import kim.jeonghyeon.net.SimpleRouting
import kim.jeonghyeon.net.addControllerBeforeInstallSimpleRouting

class SimpleFeature {
    class Configuration {
        var serviceLocator: ServiceLocator? = null
        internal var signConfig: (SignFeature.Configuration.() -> Unit)? = null
        internal var routingConfig: (SimpleRouting.Configuration.() -> Unit)? = null

        var contentNegotiationConfig: ContentNegotiation.Configuration.() -> Unit = {
            json()
        }

        var callLoggingConfig: (CallLogging.Configuration.() -> Unit)? = {}

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

        @OptIn(SimpleArchInternal::class)
        override fun install(
            pipeline: Application,
            configure: Configuration.() -> Unit
        ): SimpleFeature {
            val config = Configuration().apply(configure)

            pipeline.install(ContentNegotiation, config.contentNegotiationConfig)

            if (config.callLoggingConfig != null) {
                pipeline.install(CallLogging, config.callLoggingConfig!!)
            }


            pipeline.addControllerBeforeInstallSimpleRouting(ApiBindingController())

            pipeline.onApplicationCreate(config.serviceLocator)

            config.signConfig?.let {
                pipeline.install(SignFeature, it)
            }

            config.routingConfig?.let {
                val newConfig: SimpleRouting.Configuration.() -> Unit = {
                    hasSignFeature = pipeline.featureOrNull(SignFeature) != null
                    it()
                }

                pipeline.install(SimpleRouting, newConfig)
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