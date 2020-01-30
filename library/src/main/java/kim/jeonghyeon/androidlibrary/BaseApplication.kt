package kim.jeonghyeon.androidlibrary

import android.app.Application
import kim.jeonghyeon.androidlibrary.extension.isProdRelease
import kim.jeonghyeon.androidlibrary.extension.isTesting
import kim.jeonghyeon.androidlibrary.extension.log
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import timber.log.Timber

open class BaseApplication : Application() {
    val name: String by lazy {
        packageManager.getApplicationLabel(applicationInfo).toString()
    }

    val defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    @Suppress("RedundantModalityModifier")
    final override fun onCreate() {
        super.onCreate()
        instance = this

        if (!isProdRelease) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return String.format("L::(%s:%s)#%s",
                        element.fileName,
                        element.lineNumber,
                        element.methodName
                    )
                }
            })

            if (!isTesting) {
                //has exception on testing
                StethoHelper.initialize(this)
            }
        }
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            log(e)
            defaultUncaughtExceptionHandler.uncaughtException(t, e)
        }

        initKoin(getKoinModules())

        onCreated()
        // Normal app init code...
    }

    override fun onTerminate() {
        super.onTerminate()

        terminateKoin()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    open fun onCreated(){}

    open fun getKoinModules(): List<Module> = emptyList()

    fun initKoin(koinModules: List<Module>) {
        if (koinModules.isEmpty()) return

        startKoin {
            // use Koin logger
            androidLogger()
            androidContext(instance)
            // declare used modules
            modules(koinModules)
        }
    }

    fun terminateKoin() {
        if (getKoinModules().isEmpty()) return

        stopKoin()
    }

    companion object {
        @JvmStatic
        lateinit var instance: BaseApplication
            private set
    }

    val installTime:Long
    get() = packageManager.getPackageInfo(packageName, 0).firstInstallTime
}