package kim.jeonghyeon.file

import kim.jeonghyeon.kotlinlibrary.file.readLines
import kim.jeonghyeon.kotlinlibrary.file.write
import java.io.File

fun main() {
    File("files/data.sql").write {
        write("insert ignore into message_collect(id, message, sms_sender, receive_type, collect_type, status) values")

        File("files/data.txt").readLines { line ->
            val indexOf = line.indexOf("\t")
            val indexOf2 = line.indexOf("\t", indexOf + 1)
            val sender = line.substring(0, indexOf)
            val id = line.substring(indexOf + 1, indexOf2)
            var message = line.substring(indexOf2 + 1)
            message = message.replace("\\", "\\\\")
            message = message.replace("'", "\\'")
            message = message.replace("\"", "\\\"")
            write(
                " (\"%s\", \"%s\", \"%s\", 2, 8, 1),\n".format(
                    id, message, sender
                )
            )
        }
    }
}

