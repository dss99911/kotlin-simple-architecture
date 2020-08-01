package kim.jeonghyeon.simplearchitecture.plugin.util

import java.io.File
import java.io.FileWriter


fun File.write(action: FileWriter.() -> Unit): File {
    if (!exists()) {
        parentFile.mkdirs()
        createNewFile()
    }
    val fileWriter = FileWriter(this)
    action(fileWriter)

    fileWriter.close()
    return this
}