package kim.jeonghyeon.androidlibrary.architecture.livedata

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseLiveData
import kim.jeonghyeon.androidlibrary.architecture.repository.PreferenceCache

open class PreferenceLiveData<T>(
    val key: String,
    private val stringToValue: (String?) -> T?
) : BaseLiveData<T>(), PreferenceCache.OnSharedPreferenceChangeListener {

    override fun onFirstActive() {
        super.onFirstActive()
        loadValue()
        PreferenceCache.registerOnSharedPreferenceChangeListener(this)
    }

    fun update(value: T?, valueToString: (T?) -> String?): Boolean = PreferenceCache.setString(key, valueToString(value))

    private fun loadValue() {
        postValue(PreferenceCache.get(key, stringToValue))
    }

    override fun onSharedPreferenceChanged(key: String) {
        if (key == this.key) {
            loadValue()
        }
    }
}