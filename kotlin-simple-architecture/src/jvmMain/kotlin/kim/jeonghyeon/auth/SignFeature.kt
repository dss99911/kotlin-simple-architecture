package kim.jeonghyeon.auth

import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.sessions.Sessions
import io.ktor.util.*
import io.ktor.utils.io.core.toByteArray
import kim.jeonghyeon.net.addControllerBeforeInstallSimpleRouting

class SignFeature {
    class Configuration {
        internal val signInAuthConfigs: MutableList<SignInAuthConfiguration> = mutableListOf()

        /**
         * currently support only one service authentication
         * use [SessionServiceAuthConfiguration] or [JwtServiceAuthConfiguration]
         */
        var serviceAuthConfig: ServiceAuthConfiguration? = null

        fun basic(_config: SignBasicConfiguration.() -> Unit) {
            signInAuthConfigs.add(SignBasicConfiguration().apply(_config))
        }

        fun digest(_config: SignDigestConfiguration.() -> Unit) {
            signInAuthConfigs.add(SignDigestConfiguration().apply(_config))
        }

        fun oauth(_config: SignOAuthConfiguration.() -> Unit) {
            signInAuthConfigs.add(SignOAuthConfiguration().apply(_config))
        }

    }

    companion object Feature :
        ApplicationFeature<Application, Configuration, SignFeature> {
        override val key: AttributeKey<SignFeature> = AttributeKey("SignFeature")

        override fun install(
            pipeline: Application,
            configure: Configuration.() -> Unit
        ): SignFeature {
            val config = Configuration().apply(configure)

            val serviceAuthConfig = config.serviceAuthConfig?:error("${Configuration::serviceAuthConfig.name} is not configured on ${SignFeature::class.simpleName}")
            serviceAuthConfig.initialize(pipeline)
            selectedServiceAuthType = serviceAuthConfig.serviceAuthType

            if (config.signInAuthConfigs.isEmpty()) {
                error("${Configuration::signInAuthConfigs.name} is empty on ${SignFeature::class.simpleName}")
            }

            config.signInAuthConfigs.forEach {
                pipeline.addControllerBeforeInstallSimpleRouting(it.getController())
                it.initialize(pipeline)
            }

            return SignFeature()
        }
    }
}

@OptIn(InternalAPI::class)
internal suspend fun digest(text: String): String {
    val digest = Digest("SHA-256")
    return hex(digest.build(text.toByteArray(Charsets.UTF_8)))
}