package kim.jeonghyeon.androidlibrary

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveObject
import kim.jeonghyeon.androidlibrary.util.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import timber.log.Timber

/**
 * [onCreated] : use this, intead of [onCreate]
 * [name] : application name
 * [installTime] : application install time
 * [processLifecycle] : if application is foreground or not.
 * [isForeground] : if application is foreground or not.
 */
abstract class BaseApplication : Application(), LifecycleObserver {
    companion object {
        @JvmStatic
        lateinit var instance: BaseApplication
            private set
    }

    abstract val isProd: Boolean
    abstract val isMock: Boolean
    abstract val isDebug: Boolean

    val name: String by lazy {
        packageManager.getApplicationLabel(applicationInfo).toString()
    }

    val installTime: Long by lazy {
        packageManager.getPackageInfo(packageName, 0).firstInstallTime
    }

    val processLifecycle = LiveObject<Lifecycle.Event>()

    val defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    @Suppress("RedundantModalityModifier")
    final override fun onCreate() {
        super.onCreate()
        instance = this

        if (!isProd || isDebug) {
            initTimber()
        }
        initExceptionHandler()

        initKoin(getKoinModules())
        initLifecycle()

        onCreated()
        // Normal app init code...
    }

    @Suppress("MemberVisibilityCanBePrivate")
    open fun onCreated() {

    }

    private fun initExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Logger.log(e)
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

    private fun initLifecycle() {
        //you can use Application.ActivityLifecycleCallbacks if handling different way by activities
        ProcessLifecycleOwner
            .get()
            .lifecycle
            .addObserver(this)
    }

    override fun onTerminate() {
        super.onTerminate()

        terminateKoin()
    }

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

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onForeground() {
        processLifecycle.value = Lifecycle.Event.ON_START
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onBackground() {
        processLifecycle.value = Lifecycle.Event.ON_STOP
    }

    fun isForeground(): Boolean {
        return processLifecycle.value == Lifecycle.Event.ON_START
    }
}