package kim.jeonghyeon.kotlinlibrary.file

import java.io.*
import java.nio.charset.Charset


fun InputStream.readToString(charset: Charset = Charsets.UTF_8): String =
    this.bufferedReader(charset).use { it.readText() }

fun File.readLines(action: (String) -> Unit) {
    if (!exists()) {
        throw RuntimeException("file not exists")
    }

    BufferedReader(InputStreamReader(inputStream(), Charsets.UTF_8)).useLines { it ->
        it.iterator().forEach {
            action(it)
        }
    }
}

fun File.write(append: Boolean = false, action: FileWriter.() -> Unit) {
    val fileWriter = FileWriter(this)
    action(fileWriter)

    fileWriter.close()
}