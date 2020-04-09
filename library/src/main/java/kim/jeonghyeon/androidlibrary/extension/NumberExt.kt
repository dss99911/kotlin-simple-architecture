@file:Suppress("unused")

package kim.jeonghyeon.androidlibrary.extension

import kim.jeonghyeon.androidlibrary.BaseApplication


fun Int.getString(): String = BaseApplication.instance.getString(this)