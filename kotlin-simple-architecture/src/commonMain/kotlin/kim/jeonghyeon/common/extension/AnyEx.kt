@file:Suppress("unused")

package kim.jeonghyeon.common.extension


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

inline fun <T> T.alsoIf(predicate: (T) -> Boolean, action: (T) -> Unit): T {
    if (predicate(this)) action(this)

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

inline fun <C> C.letIf(predicate: (C) -> Boolean, data: C): C {
    if (predicate(this)) data

    return this
}