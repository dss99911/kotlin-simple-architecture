@file:Suppress("unused")

package kim.jeonghyeon.androidlibrary.extension


@Deprecated("this is ambiguous for normal Int", ReplaceWith("Int.resourceToString()"))
fun Int.getString(): String = ctx.getString(this)
fun Int.resourceToString(): String = ctx.getString(this)