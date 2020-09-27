package kim.jeonghyeon.androidlibrary

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor

import android.net.Uri
import kim.jeonghyeon.androidlibrary.extension.isDebug
import kim.jeonghyeon.util.log
import timber.log.Timber


class SimpleInitProvider : ContentProvider() {

    val defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    companion object {
        lateinit var instance: Context
            private set
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }

    override fun onCreate(): Boolean {
        instance = context!!.applicationContext

        if (isDebug) {
            initTimber()
        }
        initExceptionHandler()
        return false
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
