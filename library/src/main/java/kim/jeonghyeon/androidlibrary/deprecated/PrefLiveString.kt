package kim.jeonghyeon.androidlibrary.deprecated

import android.content.SharedPreferences
import kim.jeonghyeon.androidlibrary.extension.ctx
import kim.jeonghyeon.androidlibrary.extension.pref
import kim.jeonghyeon.androidlibrary.extension.setString

@Suppress("unused")
@Deprecated("use PreferenceLiveString")
class PrefLiveString(private val key: String, private val defValue: String? = null) : BaseLiveData<String>(), SharedPreferences.OnSharedPreferenceChangeListener {
    constructor(resId: Int, defValue: String? = null) : this(ctx.getString(resId), defValue)

    override fun onFirstActive() {
        loadValue()
        pref.registerOnSharedPreferenceChangeListener(this)
    }

    fun update(value: String?): Boolean = pref.setString(key, value)

    private fun loadValue() {
        value = pref.getString(key, defValue)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == this.key) {
            loadValue()
        }
    }
}