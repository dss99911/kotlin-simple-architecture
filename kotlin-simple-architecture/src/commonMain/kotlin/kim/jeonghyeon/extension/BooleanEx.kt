@file:Suppress("unused")

package kim.jeonghyeon.extension

inline fun <T : Boolean?> T.alsoIfTrue(action: () -> Unit): T = alsoIf(this == true) { action() }

inline fun <T : Boolean?> T.alsoIfFalse(action: () -> Unit): T = alsoIf(this == false) { action() }

inline fun <T : Boolean?> T.alsoIfNullOrFalse(action: () -> Unit): T =
    alsoIf(this == null || !this) { action() }