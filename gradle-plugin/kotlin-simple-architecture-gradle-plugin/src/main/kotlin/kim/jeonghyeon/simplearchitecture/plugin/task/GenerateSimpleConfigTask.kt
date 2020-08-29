package kim.jeonghyeon.simplearchitecture.plugin.task

import kim.jeonghyeon.simplearchitecture.plugin.extension.SimpleArchExtension
import kim.jeonghyeon.simplearchitecture.plugin.util.generatedSourceSetPath
import kim.jeonghyeon.simplearchitecture.plugin.util.getGeneratedPackageName
import kim.jeonghyeon.simplearchitecture.plugin.util.getGeneratedPackagePath
import kim.jeonghyeon.simplearchitecture.plugin.util.isMultiplatform
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun Project.getGenerateSimpleConfigTask(
    compileSourceSetName: String,
    simpleConfigProperties: MutableMap<String, String>
): TaskProvider<Task> =
    tasks.register("generateLocalAddressOn${compileSourceSetName.capitalize()}") {

        description = "generate LocalAddress of build environment"
        val outputDir = project.file(
            generatedSourceSetPath(
                project.buildDir.absolutePath,
                if (isMultiplatform) "commonMain" else compileSourceSetName
            )
        )
        //if there is no output, this task is always executed.
        //we are not sure when ip address is changed, so, execute always
        //if output file is not changed, compile won't be executed by this task.

        doLast {
            val configFile = project.file("$outputDir/${getGeneratedPackagePath()}/generated/SimpleConfig.kt")
                .also { it.parentFile.mkdirs() }

            configFile.writeText(
                """
                    |package ${getGeneratedPackageName()}.generated
                    |
                    |/** !!CAUTION!! This is Build time's local address. use this for local testing only.
                    | * - How To Test In Local -
                    | *  1. building environment and application's device should connect same network lik Wifi
                    | *  2. build application
                    | *  3. run server in local
                    | */
                    |object SimpleConfig {
                    |    const val $PROPERTY_NAME_BUILD_TIME_LOCAL_IP_ADDRESS = "${getIpAddress()}"
                    |    const val $PROPERTY_NAME_ENVIRONMENT = "${getEnvironment()}"
                    |    ${simpleConfigProperties.map { "const val ${it.key} = ${it.value}" }.joinToString("\n    ")}
                    |}
                    """.trimMargin()
            )
        }


    }


private fun getIpAddress(): String {
    val socket = Socket()
    socket.connect(InetSocketAddress("google.com", 80))
    return socket.localAddress.toString()
}

fun Project.getEnvironment(): String =
    if (hasProperty(PROPERTY_NAME_ENVIRONMENT)) property(PROPERTY_NAME_ENVIRONMENT).toString() else ""

const val PROPERTY_NAME_ENVIRONMENT = "environment"
const val PROPERTY_NAME_BUILD_TIME_LOCAL_IP_ADDRESS = "buildTimeLocalIpAddress"

fun <T> SimpleArchExtension.simpleProperty(value: () -> T): SimpleConfigDelegation<T> {
    return SimpleConfigDelegation(value, simpleProperties)
}

fun <T> SimpleArchExtension.simpleProperty(value: T): SimpleConfigDelegation<T> {
    return SimpleConfigDelegation({value}, simpleProperties)
}

class SimpleConfigDelegation<T>(val value: () -> T, val properties: MutableMap<String, String>) {
    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T> {
        val valueInvoked = value()
        properties[property.name] = valueInvoked.toLiteral()

        return object: ReadOnlyProperty<Any?, T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                return valueInvoked
            }
        }
    }

    private fun T.toLiteral(): String = when (this) {
        is String -> "\"$this\""
        else -> toString()
    }
}