@file:Suppress("unused")

package kim.jeonghyeon.kotlinlibrary.extension

import java.io.PrintWriter
import java.io.StringWriter
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

fun Throwable.getStackTraceString(): String =
    PrintWriter(StringWriter())
        .apply { printStackTrace(this) }
        .toString()

inline fun <T> ignoreException(action: () -> T): T? = try {
    action()
} catch (e: Exception) {
    null
}

inline fun <T> ignoreException(defValue: T?, action: () -> T): T? = try {
    action()
} catch (e: Exception) {
    defValue
}

inline fun <T> ignoreException(
    vararg exceptions: KClass<*> = arrayOf(java.lang.Exception::class),
    action: () -> T
): T? {
    try {
        return action()
    } catch (e: Exception) {
        e.multiCatch(*exceptions) {
            return null
        }
    }
}


inline fun <T> ignoreException(
    vararg exceptions: KClass<*> = arrayOf(java.lang.Exception::class),
    defValue: T? = null,
    action: () -> T
): T? {
    try {
        return action()
    } catch (e: Exception) {
        e.multiCatch(*exceptions) {
            return defValue
        }
    }
}

inline fun <T> catchReturnNull(
    vararg exceptions: KClass<*> = arrayOf(java.lang.Exception::class),
    action: () -> T
): T? {
    try {
        return action()
    } catch (e: Exception) {
        e.multiCatch(*exceptions) {
            return null
        }
    }
}


inline fun <R> Throwable.multiCatch(vararg classes: KClass<*>, block: () -> R): R {
    if (classes.any { this::class.isSubclassOf(it) }) {
        return block()
    } else throw this
}