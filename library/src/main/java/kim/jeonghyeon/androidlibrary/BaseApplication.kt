package kim.jeonghyeon.androidlibrary

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.squareup.picasso.OkHttp3Downloader
import okhttp3.OkHttpClient
import timber.log.Timber

open class BaseApplication : Application() {
    val name: String by lazy {
        packageManager.getApplicationLabel(applicationInfo).toString()
    }

    @Suppress("RedundantModalityModifier")
    final override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }

        LeakCanary.install(this)

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

        onCreated()
        // Normal app init code...
    }

    @Suppress("MemberVisibilityCanBePrivate")
    open fun onCreated(){}

    companion object {
        @JvmStatic
        lateinit var instance: BaseApplication
            private set
    }

    val installTime:Long
    get() = packageManager.getPackageInfo(packageName, 0).firstInstallTime
}