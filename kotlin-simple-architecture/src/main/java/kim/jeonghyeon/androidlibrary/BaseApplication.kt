package kim.jeonghyeon.androidlibrary

import android.app.Application
import kim.jeonghyeon.androidlibrary.extension.isDebug
import kim.jeonghyeon.util.log
import timber.log.Timber

/**
 * [onCreated] : use this, intead of [onCreate]
 */
abstract class BaseApplication : Application() {
    companion object {
        @JvmStatic
        lateinit var instance: BaseApplication
            private set
    }

    val defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    @Suppress("RedundantModalityModifier")
    final override fun onCreate() {
        super.onCreate()
        instance = this

        if (isDebug) {
            initTimber()
        }
        initExceptionHandler()

        onCreated()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    open fun onCreated() {
        // Normal app init code...
    }

    private fun initExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            log.e(e)
            defaultUncaughtExceptionHandler.uncaughtException(t, e)
        }
    }

    private fun initTimber() {
        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String {
                return String.format(
                    "L::(%s:%s)#%s",
                    element.fileName,
                    element.lineNumber,
                    element.methodName
                )
            }
        })
    }
}