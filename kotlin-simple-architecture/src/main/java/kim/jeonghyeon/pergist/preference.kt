package kim.jeonghyeon.pergist

import android.content.Context
import android.content.SharedPreferences
import kim.jeonghyeon.androidlibrary.extension.ctx
import kim.jeonghyeon.androidlibrary.extension.getString
import kim.jeonghyeon.androidlibrary.extension.setString
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

actual class Preference {
    private class NullValue
    interface OnSharedPreferenceChangeListener {
        fun onSharedPreferenceChanged(key: String)
    }

    private val pref: SharedPreferences by lazy {
        ctx.getSharedPreferences("simplePreference", Context.MODE_PRIVATE)
    }
    private val cacheMap = HashMap<String, Any?>()
    private val nullValue = NullValue()
    private val listeners = mutableListOf<OnSharedPreferenceChangeListener>()


    actual fun has(key: String): Boolean = getString(key) != null

    actual fun getString(key: String): String? = when {
        cacheMap[key] == nullValue -> null
        cacheMap[key] == null -> pref.getString(key).also { cacheMap[key] = it ?: nullValue }
        else -> cacheMap[key] as String?
    }

    actual fun getString(key: String, defValue: String): String {
        return getString(key) ?: defValue
    }

    actual fun getStringFlow(key: String): Flow<String?> = flow {
        val channel = Channel<Unit>(Channel.CONFLATED).apply { offer(Unit) }

        val listener = object : OnSharedPreferenceChangeListener {
            override fun onSharedPreferenceChanged(updatedKey: String) {
                if (updatedKey == key) {
                    channel.offer(Unit)
                }
            }
        }
        listeners.add(listener)
        try {
            for (unit in channel) {
                emit(getString(key))
            }
        } finally {
            listeners.remove(listener)
        }
    }

    actual fun setString(key: String, value: String?) {
        pref.setString(key, value)
            .also { if (it) cacheMap[key] = value ?: nullValue }
            .also { listeners.forEach { it.onSharedPreferenceChanged(key) } }
    }
}