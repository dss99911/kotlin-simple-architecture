package kim.jeonghyeon.androidlibrary.extension

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import com.google.gson.JsonObject
import kotlin.reflect.KClass

fun intent(cls: KClass<*>): Intent = Intent(ctx, cls.java)
inline fun <reified T> intent(): Intent = Intent(ctx, T::class.java)
fun intentFilter(vararg actions: String): IntentFilter = IntentFilter()
    .apply {
        actions.forEach { addAction(it) }
    }

fun Bundle.toJsonString(): String {
    val obj = JsonObject()
    keySet().forEach {
        obj.addProperty(it, get(it)?.toString())
    }

    return obj.toString()
}