@file:Suppress("unused")

package kim.jeonghyeon.jvm.extension

import kim.jeonghyeon.common.extension.letIfNotEmpty
import java.io.File

@Deprecated("old code")
class SystemCommandExecutor(
    private val logOnError: Boolean = false,
    private val logCommand: Boolean = false
) {
    /**
     * @param command command to execute
     * @param resultAndMessage success and result message, fail and fail message, and return boolean to keep progress on next step
     * @return this if resultAndMessage return true or null if resultAndMessage return false
     */
    fun execute(
        command: String,
        path: String? = null,
        print: Boolean = false,
        resultAndMessage: (success: Boolean, message: String) -> Boolean = { success, _ -> success }
    ): SystemCommandExecutor? {
        if (logCommand) print("[Command] $command")
        val p: Process =
            if (path == null) Runtime.getRuntime().exec(command)
            else Runtime.getRuntime().exec(command, null, File(path))

        p.inputStream.readToString().letIfNotEmpty {
            if (print) print("[Success] $it")

            return if (resultAndMessage(true, it)) this
            else null
        }


        p.errorStream.readToString().letIfNotEmpty {
            if (logOnError || print) print("[Error] $it")

            return if (resultAndMessage(false, it)) this
            else null
        }

        return this
    }

    /**
     * input by array in case of using quotes
     * ex) = mutableListOf("git", "log", "--author=$authors", "--all", "--after=\"${after.format("yyyy-MM-dd HH:mm:ss")}\"", "--oneline", "--no-merges")
     */
    fun execute(
        command: Array<String>,
        path: String? = null,
        print: Boolean = false,
        resultAndMessage: (success: Boolean, message: String) -> Boolean = { success, _ -> success }
    ): SystemCommandExecutor? {
        if (logCommand) {
            print("[Command] $command")
        }

        val p: Process =
            if (path == null) Runtime.getRuntime().exec(command)
            else Runtime.getRuntime().exec(command, null, File(path))

        p.inputStream.readToString().letIfNotEmpty {
            if (print) print("[Success] $it")

            return if (resultAndMessage(true, it)) this
            else null
        }


        p.errorStream.readToString().letIfNotEmpty {
            if (logOnError || print) print("[Error] $it")

            return if (resultAndMessage(false, it)) this
            else null
        }

        return this
    }

}
