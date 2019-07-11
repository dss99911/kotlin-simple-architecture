@file:Suppress("unused")

package kim.jeonghyeon.androidlibrary.extension

import android.preference.Preference
import android.preference.PreferenceScreen
import androidx.annotation.StringRes

fun PreferenceScreen.findPreference(@StringRes resId: Int): Preference? = this.findPreference(ctx.getString(resId))

fun androidx.preference.PreferenceScreen.findPreference(@StringRes resId: Int): androidx.preference.Preference = findPreference(ctx.getString(resId))