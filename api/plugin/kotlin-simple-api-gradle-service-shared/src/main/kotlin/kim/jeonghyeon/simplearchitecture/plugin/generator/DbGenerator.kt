package kim.jeonghyeon.simplearchitecture.plugin.generator

import com.squareup.sqldelight.Transacter
import kim.jeonghyeon.simplearchitecture.plugin.model.*
import kim.jeonghyeon.simplearchitecture.plugin.util.generatedSourceSetPath
import kim.jeonghyeon.simplearchitecture.plugin.util.write
import java.io.File

/**
 * filter interface which implement `Transacter`, and Companion object contains `Schema: SqlDriver.Schema
 *
 * generated code sample
 *
 * import com.squareup.sqldelight.android.AndroidSqliteDriver
 * import kim.jeonghyeon.sample.SampleDb
 * import com.squareup.sqldelight.Transacter
 * import kim.jeonghyeon.androidlibrary.extension.ctx
 *
 * inline fun <reified T> db(name: String = "${T::class.simpleName}.db") {
 *      if (T::class == SampleDb::class) {
 *          SampleDb(AndroidSqliteDriver(SampleDb.Schema, ctx, name))
 *      }
 * }
 */
class DbGenerator(
    private val pluginOptions: PluginOptions,
    private val origin: Collection<SharedKtFile>
) {

    /**
     * generate expect file on common target if it's multiplatform
     * generate actual file on compile source set
     */
    fun generate(): Collection<File> {
        if (pluginOptions.platformType == KotlinPlatformType.js) {
            //todo sqldelight not yet support js
            return emptyList<GeneratedDbSource>().generateDbFunctionFile()
        }

        return origin
            .flatMap { it.generatedDbSources }
            .generateDbFunctionFile()
    }

    private val SharedKtFile.generatedDbSources
        get(): List<GeneratedDbSource> = getChildrenOfKtClass()
            .filter { it.isDbInterface() }
            .map {
                GeneratedDbSource(
                    it.name,
                    packageFqName,
                    pluginOptions.getGeneratedTargetVariantsPath()
                )
            }


    /**
     * filter interface which implement `Transacter`, and Companion object contains property 'Schema'
     */
    private fun SharedKtClass.isDbInterface(): Boolean {
        return isInterface()
                && superTypeText == Transacter::class.simpleName
                && containingKtFile.hasImport(Transacter::class)
                && hasCompanionPropertyName("Schema")
    }

    private fun List<GeneratedDbSource>.generateDbFunctionFile(): List<File> {
        var expectFile: File? = null
        val filePath = "${pluginOptions.packagePath}/generated/db/Db${pluginOptions.postFix.capitalize()}Ex.kt"
        if (pluginOptions.isMultiplatform) {
            val expectPath = generatedSourceSetPath(
                pluginOptions.buildPath,
                SOURCE_SET_NAME_COMMON
            )
            expectFile = File("$expectPath/$filePath")
                .takeIf { !it.exists() } //e: java.lang.IllegalStateException: Unable to collect additional sources in reasonable number of iterations
                ?.write {
                    append(
                        """
                        // $GENERATED_FILE_COMMENT
                        package ${pluginOptions.packageName}.generated.db

                        import com.squareup.sqldelight.Transacter
                        
                        /**
                         * This is Sqlite DB
                         * @param path if it's JVM, it's url of sql driver. jdbc:sqlite:
                         * @param properties this is used for JVM only.
                         */
                        expect inline fun <reified T : Transacter> db${pluginOptions.postFix.capitalize()}(path: String = T::class.simpleName + ".db", properties: Map<String?, String?> = emptyMap()): T

                        """.trimIndent()
                    )
                }
        }

        if (pluginOptions.platformType == KotlinPlatformType.common) {
            return listOfNotNull(expectFile)
        }

        val actualPath = pluginOptions.getGeneratedTargetVariantsPath().let {
            File("$it/$filePath")
                .takeIf { !it.exists() }//e: java.lang.IllegalStateException: Unable to collect additional sources in reasonable number of iterations
                ?.write {
                append(
                    """
                |// $GENERATED_FILE_COMMENT
                |package ${pluginOptions.packageName}.generated.db
                |
                |${makeImport()}
                |${getElseImport()}
                |
                |${if (pluginOptions.isMultiplatform) "actual " else ""}inline fun <reified T : Transacter> db${pluginOptions.postFix.capitalize()}(path: String, properties: Map<String?, String?>): T {
                |
                |    return when (T::class) {
                |${joinToString("\n") { "${it.name}::class -> ${it.name}(${it.makeDriverInstance(it.name)})" }.prependIndent("        ")}
                |
                |        else -> ${getElseStatement()}
                |    } as T
                |}
                """.trimMargin()
                )
            }
        }
        return listOfNotNull(expectFile, actualPath)
    }

    fun getElseImport(): String {
        val isOtherWithArchitecture = !pluginOptions.isInternal && pluginOptions.useFramework

        return if (isOtherWithArchitecture) {
            "import kotlinsimplearchitectureclient.generated.db.dbSimpleFramework"
        } else {
            ""
        }

    }

    fun getElseStatement(): String {
        val isOtherWithArchitecture = !pluginOptions.isInternal && pluginOptions.useFramework

        return if (isOtherWithArchitecture) {
            "dbSimpleFramework<T>(path, properties)"
        } else {
            "error(\"can not create database name \" + T::class.simpleName)"
        }
    }


    fun List<GeneratedDbSource>.makeImport(): String {
        return when (pluginOptions.platformType) {
            KotlinPlatformType.androidJvm -> """
                import com.squareup.sqldelight.android.AndroidSqliteDriver
                import kim.jeonghyeon.androidlibrary.extension.ctx
            """.trimIndent()

            KotlinPlatformType.native -> """
                import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
                
            """.trimIndent()
            KotlinPlatformType.jvm -> """
                import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
                import java.util.*
            """.trimIndent()
            KotlinPlatformType.js -> ""//todo js is not yet supported
            else -> {
                error("${pluginOptions.platformType} target's DB driver is not yet supported")
            }
        } + """
            |
            |import com.squareup.sqldelight.Transacter
            |${joinToString("\n|") { "import " + it.packageName + "." + it.name }}
        """.trimMargin()

    }

    fun GeneratedDbSource.makeDriverInstance(dbName: String): String {
        return when (pluginOptions.platformType) {
            KotlinPlatformType.androidJvm -> {
                "AndroidSqliteDriver(${name}.Schema, ctx, path)"
            }
            KotlinPlatformType.native -> {
                "NativeSqliteDriver(${name}.Schema, path)"
            }
            KotlinPlatformType.jvm -> {
                "JdbcSqliteDriver(\"jdbc:sqlite:\" + path, Properties().apply { properties.forEach { this.setProperty(it.key, it.value) } }).also { try { $dbName.Schema.create(it) } catch (e: Exception) { } }"
            }
            else -> {
                error("${pluginOptions.platformType} target's DB driver is not yet supported")
            }
        }
    }
}


