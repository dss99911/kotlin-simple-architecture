@file:Suppress("unused")

package kim.jeonghyeon.kotlinlibrary.extension

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * use also()
 */
//inline fun <T> T.act(action: (T) -> Unit): T {
//    action(this)
//    return this
//}

fun <T> T.println(): T {
    println(this)
    return this
}

inline fun <T> T?.ifNull(action: (T?) -> T): T {
    if (this !== null) {
        return this
    }

    return action(this)
}

inline fun <T : CharSequence> T?.ifNullOrEmpty(action: (T?) -> T?): T? =
    if (this.isNullOrEmpty()) {
        action(this)
    } else this

inline fun <T> T?.onNull(action: (T?) -> Unit): T? {
    if (this === null) {
        action(this)
    }
    return this
}

fun Any?.printAsJson() {
    println(
        GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create()
            .toJson(this)
    )
}

fun Any?.toJsonString(): String? = if (this == null) null else Gson().toJson(this)

@Deprecated("use alsoIf")
inline fun <T> T.processIf(predicate: (T) -> Boolean, action: (T) -> Unit): T {
    if (predicate(this)) action(this)

    return this
}

inline fun <T> T.alsoIf(predicate: (T) -> Boolean, action: (T) -> Unit): T {
    if (predicate(this)) action(this)

    return this
}

@Deprecated("use alsoIf")
inline fun <T> T.processIf(predicate: Boolean, action: (T) -> Unit): T {
    if (predicate) action(this)

    return this
}

inline fun <T> T.alsoIf(predicate: Boolean, action: (T) -> Unit): T {
    if (predicate) also(action)

    return this
}

inline fun <C> C.letIf(predicate: Boolean, action: (C) -> C): C {
    if (predicate) let(action)

    return this
}

inline fun <C> C.letIf(predicate: (C) -> Boolean, action: (C) -> C): C {
    if (predicate(this)) let(action)

    return this
}
