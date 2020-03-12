package kim.jeonghyeon.sample.etc.storage

import android.content.Context
import kim.jeonghyeon.androidlibrary.extension.ctx

fun saveFile() {
    ctx.openFileOutput("name", Context.MODE_PRIVATE).use { fos ->
        fos.write("text".toByteArray())
    }
}

fun loadFile() {
    // The file name cannot contain path separators.
    val FILE_NAME = "sensitive_info.txt"
    val fis = ctx.openFileInput(FILE_NAME)

// available() determines the approximate number of bytes that can be
// read without blocking.
    val bytesAvailable = fis.available()
    val fileBuffer = ByteArray(bytesAvailable)
    val topSecretFileContents = StringBuilder(bytesAvailable).apply {
        // Make sure that read() returns a number of bytes that is equal to the
        // file's size.
        while (fis.read(fileBuffer) != -1) {
            append(fileBuffer)
        }
    }
}