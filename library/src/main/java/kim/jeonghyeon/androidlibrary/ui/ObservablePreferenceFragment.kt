package kim.jeonghyeon.androidlibrary.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroup
import androidx.preference.SwitchPreference

/**
 * limitation : if use same key on different preference. it is not supported
 */
abstract class ObservablePreferenceFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    abstract val preferenceResId: Int
    private val preferenceMap: HashMap<String, Preference> = hashMapOf()

    @CallSuper
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(preferenceResId, rootKey)
        addPreferenceGroupToMap(preferenceScreen)
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this@ObservablePreferenceFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this@ObservablePreferenceFragment)
    }

    private fun addPreferenceGroupToMap(group: PreferenceGroup) {
        addPreferenceToMap(group)

        for (i in 0..(group.preferenceCount - 1)) {
            val preference = group.getPreference(i)
            if (preference is PreferenceGroup) addPreferenceGroupToMap(preference)
            else addPreferenceToMap(preference)
        }
    }

    private fun addPreferenceToMap(preference: Preference) {
        if (preference.hasKey() && preference.isPersistent) {
            if (preferenceMap.containsKey(preference.key)) {
                throw IllegalStateException("same key on several Preference is not supported")
            }
            preferenceMap[preference.key] = preference
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences == null) return
        val preference = preferenceMap[key] ?: return

        when (preference) {
            is SwitchPreference -> {
                preference.isChecked = sharedPreferences.getBoolean(key, false)
            }
            else -> throw IllegalStateException(preference::class.java.simpleName + " is not supported onSharedPreference changed")
        }
    }
}