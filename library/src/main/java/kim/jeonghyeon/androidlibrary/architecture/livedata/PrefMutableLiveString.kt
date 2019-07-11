package kim.jeonghyeon.androidlibrary.architecture.livedata

import android.content.SharedPreferences
import androidx.lifecycle.Observer
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseMutableLiveData
import kim.jeonghyeon.androidlibrary.extension.ctx
import kim.jeonghyeon.androidlibrary.extension.pref
import kim.jeonghyeon.androidlibrary.extension.setString

@Suppress("unused")
class PrefMutableLiveString(private val key: String, private val defValue: String? = null) : BaseMutableLiveData<String>(), Observer<String>, SharedPreferences.OnSharedPreferenceChangeListener {
    constructor(resId: Int, defValue: String? = null) : this(ctx.getString(resId), defValue)

    override fun onFirstActive() {
        loadValue()
        observeForever(this)
        pref.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onChanged(t: String?) {
        pref.setString(key, value)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == this.key) {
            removeObserver(this)
            loadValue()
            observeForever(this)
        }
    }

    private fun loadValue() {
        val string = pref.getString(key, defValue)
        if (string != value) {
            value = string
        }
    }
}