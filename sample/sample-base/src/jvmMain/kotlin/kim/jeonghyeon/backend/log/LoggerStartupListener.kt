package kim.jeonghyeon.backend.log

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.LoggerContextListener
import ch.qos.logback.core.spi.ContextAwareBase
import ch.qos.logback.core.spi.LifeCycle
import samplebase.generated.SimpleConfig

class LoggerStartupListener : ContextAwareBase(), LoggerContextListener, LifeCycle {
    private var started = false
    override fun start() {
        if (started) return

        configure()

        started = true
    }

    private fun configure() {
        /**
         * todo Environment is created after Logger is initialized, so, can't bring this from application.conf
         */
        val logPath = if (SimpleConfig.isProduction) "/home/ec2-user/app/sample-backend" else "."
        val logLevel = "TRACE"

        getContext().apply {
            context.putProperty("LOG_PATH", logPath)
            context.putProperty("LOG_LEVEL", logLevel)
        }
    }

    data class Environment(
        val logPath: String,
        val logLevel: String,
        val dbPath: String
    )

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