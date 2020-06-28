@file:Suppress("unused")

package kim.jeonghyeon.androidlibrary.extension

import android.content.SharedPreferences
import androidx.annotation.StringRes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
//todo move to Preference implementation
fun SharedPreferences.getBoolean(@StringRes resId: Int, defValue: Boolean): Boolean =
        getBoolean(ctx.getString(resId), defValue)

fun SharedPreferences.getBoolean(@StringRes resId: Int) =
        getBoolean(ctx.getString(resId), false)

fun SharedPreferences.getString(key: String): String? =
        getString(key, null)
fun SharedPreferences.getString(@StringRes resId: Int): String? =
        getString(ctx.getString(resId))

fun SharedPreferences.getStringSet(@StringRes resId: Int): Set<String>? =
        getStringSet(ctx.getString(resId), null)

fun SharedPreferences.getFloat(@StringRes resId: Int, defValue: Float) =
        getFloat(ctx.getString(resId), defValue)

fun SharedPreferences.getFloat(@StringRes resId: Int) =
        getFloat(ctx.getString(resId), 0f)

inline fun <reified T> SharedPreferences.getFromJson(key: String): T? =
        noThrow {
            if (T::class == List::class) {
                Gson().fromJson<T>(getString(key, null), object : TypeToken<T>(){}.type)
            } else Gson().fromJson<T>(getString(key, null), T::class.java)
        }
inline fun <reified T> SharedPreferences.getFromJson(key: Int): T? =
        noThrow { Gson().fromJson<T>(getString(ctx.getString(key), null), T::class.java) }
fun <T> SharedPreferences.getFromJson(cls: Class<T>, key: String): T? =
        noThrow {
            Gson().fromJson<T>(getString(key, null), cls)
        }


fun SharedPreferences.applyBoolean(@StringRes resId: Int, value: Boolean) =
        edit().putBoolean(ctx.getString(resId), value).apply()

fun SharedPreferences.Editor.putString(@StringRes resId: Int, value: String): SharedPreferences.Editor =
        putString(ctx.getString(resId), value)

fun SharedPreferences.Editor.putStringSet(@StringRes resId: Int, value: Set<String>): SharedPreferences.Editor =
        putStringSet(ctx.getString(resId), value)

fun SharedPreferences.Editor.putToJson(key: String, value: Any?): SharedPreferences.Editor =
        putString(key, Gson().toJson(value))

fun SharedPreferences.Editor.putFloat(@StringRes resId: Int, value: Float): SharedPreferences.Editor =
        putFloat(ctx.getString(resId), value)

fun SharedPreferences.Editor.putBoolean(@StringRes resId: Int, value: Boolean): SharedPreferences.Editor =
        putBoolean(ctx.getString(resId), value)

fun SharedPreferences.setFloat(@StringRes resId: Int, value: Float) =
        edit().putFloat(ctx.getString(resId), value).commit()

fun SharedPreferences.setBoolean(@StringRes resId: Int, value: Boolean) =
        edit().putBoolean(ctx.getString(resId), value).commit()

fun SharedPreferences.setBoolean(key: String, value: Boolean) =
        edit().putBoolean(key, value).commit()

fun SharedPreferences.setString(key: String, value: String?) =
        edit().putString(key, value).commit()

fun SharedPreferences.setString(key: Int, value: String?) =
        setString(ctx.getString(key), value)
fun SharedPreferences.setJson(key: String, value: Any?) =
        edit().putToJson(key, value).commit()
fun SharedPreferences.setJson(key: Int, value: Any?) =
        setJson(ctx.getString(key), value)