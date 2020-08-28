package kim.jeonghyeon.backend.log

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.LoggerContextListener
import ch.qos.logback.core.spi.ContextAwareBase
import ch.qos.logback.core.spi.LifeCycle
import kim.jeonghyeon.backend.di.serviceLocator

class LoggerStartupListener : ContextAwareBase(), LoggerContextListener, LifeCycle {
    private var started = false
    override fun start() {
        if (started) return

        configure()

        started = true
    }

    private fun configure() {
        /**
         * Application is created after Logger is initialized, so, can't bring this from application.conf
         */
        val logPath = serviceLocator.environment.logPath
        val logLevel = serviceLocator.environment.logLevel

        getContext().apply {
            context.putProperty("LOG_PATH", logPath)
            context.putProperty("LOG_LEVEL", logLevel)
        }
    }

    override fun stop() {}
    override fun isStarted(): Boolean {
        return started
    }

    override fun isResetResistant(): Boolean {
        return true
    }

    override fun onStart(context: LoggerContext) {}
    override fun onReset(context: LoggerContext) {}
    override fun onStop(context: LoggerContext) {}
    override fun onLevelChange(logger: Logger, level: Level) {}
}