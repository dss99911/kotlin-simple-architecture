@file:Suppress("unused")

package kim.jeonghyeon.jvm.extension

import java.io.InputStream
import java.nio.charset.Charset

//todo delete?
fun InputStream.readToString(charset: Charset = Charsets.UTF_8): String =
    this.bufferedReader(charset).use { it.readText() }