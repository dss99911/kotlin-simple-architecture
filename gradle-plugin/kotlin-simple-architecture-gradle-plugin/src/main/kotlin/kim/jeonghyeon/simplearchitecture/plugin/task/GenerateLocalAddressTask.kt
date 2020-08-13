package kim.jeonghyeon.simplearchitecture.plugin.task

import kim.jeonghyeon.simplearchitecture.plugin.util.generatedSourceSetPath
import kim.jeonghyeon.simplearchitecture.plugin.util.getPackageName
import kim.jeonghyeon.simplearchitecture.plugin.util.getPackagePath
import kim.jeonghyeon.simplearchitecture.plugin.util.isMultiplatform
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import java.net.InetSocketAddress
import java.net.Socket


//todo when ip address changed, generated file is not updated.
fun Project.getGenerateLocalAddressTask(compileSourceSetName: String): TaskProvider<Task> =
    tasks.register("generateLocalAddressOn${compileSourceSetName.capitalize()}") {
        description = "generate LocalAddress of build environment"
        val outputDir = project.file(
            generatedSourceSetPath(
                project.buildDir.absolutePath,
                if (isMultiplatform) "commonMain" else compileSourceSetName
            )
        )

        outputs.dir(outputDir)

        doLast {
            val configFile = project.file("$outputDir/${getPackagePath()}/generated/SimpleConfig.kt")
            configFile.parentFile.mkdirs()

            val socket = Socket()
            socket.connect(InetSocketAddress("google.com", 80))

            configFile.writeText(
                """
                    |package ${getPackageName()}.generated
                    |
                    |/** !!CAUTION!! This is Build time's local address. use this for local testing only.
                    | * - How To Test In Local -
                    | *  1. building environment and application's device should connect same network lik Wifi
                    | *  2. build application
                    | *  3. run server in local
                    | *  4. todo if build is passed with no source code change. but ip address changed. this is not changed. find the solution
                    | */
                    |object SimpleConfig {
                    |    val BUILD_TIME_LOCAL_IP_ADDRESS = "${socket.localAddress}"
                    |}
                    """.trimMargin()
            )
        }
    }

