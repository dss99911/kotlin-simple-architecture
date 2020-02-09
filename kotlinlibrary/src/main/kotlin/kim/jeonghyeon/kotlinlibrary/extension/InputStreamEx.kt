@file:Suppress("unused")

package kim.jeonghyeon.kotlinlibrary.extension

import java.io.InputStream
import java.nio.charset.Charset

fun InputStream.readToString(charset: Charset = Charsets.UTF_8): String =
    this.bufferedReader(charset).use { it.readText() }