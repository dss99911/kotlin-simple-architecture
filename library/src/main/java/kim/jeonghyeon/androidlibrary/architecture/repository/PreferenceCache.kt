package kim.jeonghyeon.androidlibrary.architecture.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import kim.jeonghyeon.androidlibrary.extension.getString
import kim.jeonghyeon.androidlibrary.extension.pref
import kim.jeonghyeon.androidlibrary.extension.setString
import kim.jeonghyeon.kotlinlibrary.type.WeakArrayList

/**
 * currently, no consideration of recycle, because, not much data is used.
 */
object PreferenceCache {

    interface OnSharedPreferenceChangeListener {
        fun onSharedPreferenceChanged(key: String)
    }

    private val cacheMap = HashMap<String, Any?>()
    private val listeners = WeakArrayList<OnSharedPreferenceChangeListener>()

    fun getString(key: String): String? = when {
        cacheMap[key] == nullValue -> null
        cacheMap[key] == null -> pref.getString(key).also { cacheMap[key] = it ?: nullValue }
        else -> cacheMap[key] as String?
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, formatting: (String?) -> T?): T? = when {
        cacheMap[key] == nullValue -> null
        cacheMap[key] == null -> formatting(pref.getString(key)).also { cacheMap[key] = it ?: nullValue }
        else -> cacheMap[key] as T?
    }

    fun setString(key: String, value: String?): Boolean =
        pref.setString(key, value)
                    .also { if (it) cacheMap[key] = value ?: nullValue }
                    .also { listeners.forEachWeakReference { it.onSharedPreferenceChanged(key) } }

    fun <T> set(key: String, value: T?, formatting: (T?) -> String?): Boolean =
        pref.setString(key, formatting(value))
                    .also { if (it) cacheMap[key] = value ?: nullValue }
                    .also { listeners.forEachWeakReference { it.onSharedPreferenceChanged(key) } }

    inline fun <reified T> get(key: String) =
            get(key) {
                Gson().fromJson(it, T::class.java)
            }

    fun <T> set(key: String, value: T?) =
            set(key, value) {
                if (value == null) null
                else Gson().toJson(value)
            }

    fun registerOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        listeners.addWeakReference(listener)
    }

    private val nullValue = NullValue()

    class NullValue
}