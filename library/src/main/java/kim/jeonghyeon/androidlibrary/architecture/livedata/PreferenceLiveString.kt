package kim.jeonghyeon.androidlibrary.architecture.livedata

import androidx.annotation.IdRes
import kim.jeonghyeon.androidlibrary.architecture.repository.PreferenceCache
import kim.jeonghyeon.androidlibrary.deprecated.BaseLiveData
import kim.jeonghyeon.androidlibrary.extension.getString

/**
 * load data from preference
 * if data is updated, it will be notified
 *
 * Consideration
 * - It should not update when setValue(), cuz if do that, if setValue(), and save failed. but LiveData already notify updated value. so, please call update()
 */
class PreferenceLiveString(val key: String) : BaseLiveData<String>(), PreferenceCache.OnSharedPreferenceChangeListener {


    constructor(@IdRes resId: Int) : this(resId.getString())

    override fun onFirstActive() {
        super.onFirstActive()
        loadValue()
        PreferenceCache.registerOnSharedPreferenceChangeListener(this)
    }

    fun update(value: String?): Boolean = PreferenceCache.setString(key, value)

    private fun loadValue() {
        postValue(PreferenceCache.getString(key))
    }

    override fun onSharedPreferenceChanged(key: String) {
        if (key == this.key) {
            loadValue()
        }
    }
}