package kim.jeonghyeon.jvm.extension

import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader

//todo delete?
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
    if (!exists()) {
        parentFile.mkdirs()
        createNewFile()
    }

    val fileWriter = FileWriter(this)
    action(fileWriter)

    fileWriter.close()
}