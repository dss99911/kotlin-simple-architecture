@file:Suppress("unused")

package kim.jeonghyeon.common.extension


fun <T> T.println(): T = also { println(it) }

inline fun <T> T?.notNull(action: () -> T): T {
    if (this !== null) {
        return this
    }

    return action()
}

inline fun <T> T.alsoIf(predicate: Boolean, action: (T) -> Unit): T =
    also { if (predicate) action(this) }

inline fun <T> T.alsoIf(predicate: (T) -> Boolean, action: (T) -> Unit): T =
    alsoIf(predicate(this), action)

inline fun <T> T?.alsoIfNull(action: () -> Unit): T? = also { if (this == null) action() }

inline fun <C> C.letIf(predicate: Boolean, action: (C) -> C): C =
    if (predicate) let(action) else this

inline fun <C> C.letIf(predicate: (C) -> Boolean, action: (C) -> C): C =
    letIf(predicate(this), action)

inline fun <C> C.letIf(predicate: (C) -> Boolean, data: C): C = letIf(predicate(this)) { data }