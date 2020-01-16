package kim.jeonghyeon.androidlibrary

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import timber.log.Timber

open class BaseApplication : Application() {
    val name: String by lazy {
        packageManager.getApplicationLabel(applicationInfo).toString()
    }

    @Suppress("RedundantModalityModifier")
    final override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return String.format("L::(%s:%s)#%s",
                        element.fileName,
                        element.lineNumber,
                        element.methodName
                    )
                }
            })
        }

        StethoHelper.initialize(this)

        instance = this

        initKoin(getKoinModules())

        onCreated()
        // Normal app init code...
    }

    @Suppress("MemberVisibilityCanBePrivate")
    open fun onCreated(){}

    open fun getKoinModules(): List<Module> = emptyList()

    fun initKoin(koinModules: List<Module>) {
        if (koinModules.isEmpty()) return

        startKoin {
            // use Koin logger
            androidLogger()
            androidContext(this@BaseApplication)
            // declare used modules
            modules(koinModules)
        }
    }

    companion object {
        @JvmStatic
        lateinit var instance: BaseApplication
            private set
    }

    val installTime:Long
    get() = packageManager.getPackageInfo(packageName, 0).firstInstallTime
}