package kim.jeonghyeon.androidlibrary.architecture.livedata

import android.content.SharedPreferences
import com.google.gson.Gson
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseLiveData
import kim.jeonghyeon.androidlibrary.extension.ctx
import kim.jeonghyeon.androidlibrary.extension.getFromJson
import kim.jeonghyeon.androidlibrary.extension.pref
import kim.jeonghyeon.androidlibrary.extension.setJson
import kim.jeonghyeon.kotlinlibrary.extension.ifNull

@Suppress("unused")
@Deprecated("use PreferenceLiveData")
class PrefLiveData<T>(private val cls: Class<T>, private val key: String, private val defValue: T? = null) : BaseLiveData<T>(), SharedPreferences.OnSharedPreferenceChangeListener {
    constructor(cls: Class<T>, resId: Int, defValue: T? = null) : this(cls, ctx.getString(resId), defValue)

    override fun onFirstActive() {
        loadValue()
        pref.registerOnSharedPreferenceChangeListener(this)
    }

    fun update(value: T?): Boolean = pref.setJson(key,
            if (value == null) null
            else Gson().toJson(value)
    )

    private fun loadValue() {
        value = pref.getFromJson(cls, key).ifNull { defValue }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == this.key) {
            loadValue()
        }
    }


}