@file:Suppress("unused")

package kim.jeonghyeon.androidlibrary.extension


fun Int.resourceToString(): String = ctx.getString(this)