package kim.jeonghyeon.auth

import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.util.AttributeKey
import kim.jeonghyeon.net.addControllerBeforeInstallSimpleRouting

class SignFeature {
    class Configuration {
        var authConfig: SignAuthConfiguration? = null

        fun basic(_config: SignBasicConfiguration.() -> Unit) {
            authConfig = SignBasicConfiguration().apply(_config)
        }

        fun digest(_config: SignDigestConfiguration.() -> Unit) {
            authConfig = SignDigestConfiguration().apply(_config)
        }

        fun jwt(_config: SignJwtConfiguration.() -> Unit) {
            authConfig = SignJwtConfiguration().apply(_config)
        }

    }

    companion object Feature :
        ApplicationFeature<Application, Configuration, SignFeature> {
        override val key: AttributeKey<SignFeature> = AttributeKey("SignFeature")

        override fun install(
            pipeline: Application,
            configure: Configuration.() -> Unit
        ): SignFeature {
            val config = Configuration().apply(configure).authConfig!!
            authType = config.authType
            pipeline.addControllerBeforeInstallSimpleRouting(config.getController())

            config.initialize(pipeline)
            return SignFeature()
        }
    }
}

abstract class SignAuthConfiguration(internal val authType: AuthenticationType) {
    internal abstract fun getController(): SignController
    abstract fun initialize(pipeline: Application)
}
