package kim.jeonghyeon.simplearchitecture.plugin.generator

import com.squareup.sqldelight.Transacter
import kim.jeonghyeon.simplearchitecture.plugin.model.GeneratedDbSource
import kim.jeonghyeon.simplearchitecture.plugin.model.PluginOptions
import kim.jeonghyeon.simplearchitecture.plugin.model.SOURCE_SET_NAME_COMMON
import kim.jeonghyeon.simplearchitecture.plugin.util.generatedSourceSetPath
import kim.jeonghyeon.simplearchitecture.plugin.util.hasImport
import kim.jeonghyeon.simplearchitecture.plugin.util.write
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
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
    private val origin: Collection<KtFile>
) {

    /**
     * generate expect file on common target if it's multiplatform
     * generate actual file on compile source set
     */
    fun generate(): Collection<File> {

        return origin
            .flatMap { it.generatedDbSources }
            .generateDbFunctionFile()
    }

    private val KtFile.generatedDbSources
        get(): List<GeneratedDbSource> = getChildrenOfType<KtClass>()
            .filter { it.isDbInterface() }
            .map {
                GeneratedDbSource(
                    it.name!!,
                    packageFqName.asString(),
                    pluginOptions.getGeneratedTargetVariantsPath()
                )
            }


    /**
     * filter interface which implement `Transacter`, and Companion object contains property 'Schema'
     */
    private fun KtClass.isDbInterface(): Boolean {
        return name != null
                && isInterface()
                && findDescendantOfType<KtSuperTypeList>()?.text == Transacter::class.simpleName
                && containingKtFile.hasImport<Transacter>()
                && findDescendantOfType<KtObjectDeclaration> { it.isCompanion() }?.findDescendantOfType<KtProperty> { it.name == "Schema" } != null
    }

    private fun List<GeneratedDbSource>.generateDbFunctionFile(): List<File> {
        var expectFile: File? = null
        val filePath = "kim/jeonghyeon/generated/db/DbEx.kt"
        if (pluginOptions.isMultiplatform) {
            val expectPath = generatedSourceSetPath(
                pluginOptions.buildPath,
                SOURCE_SET_NAME_COMMON
            )
            expectFile = File("$expectPath/$filePath")
                .takeIf { !it.exists() }
                ?.write {
                    append(
                        """
                        // $GENERATED_FILE_COMMENT
                        package kim.jeonghyeon.generated.db

                        import com.squareup.sqldelight.Transacter
                        
                        expect inline fun <reified T : Transacter> db(name: String = T::class.simpleName + ".db"): T

                        """.trimIndent()
                    )
                }
        }

        val actualPath = pluginOptions.getGeneratedTargetVariantsPath().let {
            File("$it/$filePath").takeIf { !it.exists() }?.write {
                append(
                    """
                |// $GENERATED_FILE_COMMENT
                |package kim.jeonghyeon.generated.db
                |
                |${makeImport()}
                |
                |${if (pluginOptions.isMultiplatform) "actual " else ""}inline fun <reified T : Transacter> db(name: String): T {
                |
                |${INDENT}return when (T::class) {
                |${joinToString("\n") { "${it.name}::class -> ${it.name}(${it.makeDriverInstance()})" }.prependIndent(indent(2))}
                |
                |$INDENT${INDENT}else -> error("can not create database name " + T::class.qualifiedName)
                |$INDENT} as T
                |}
                """.trimMargin()
                )
            }
        }
        return listOfNotNull(actualPath, expectFile)
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

            else -> {
                error("${pluginOptions.platformType} target's DB driver is not yet supported")
            }
        } + """
            import com.squareup.sqldelight.Transacter
            ${joinToString("\n") { "import " + it.packageName + "." + it.name }}
        """.trimIndent()

    }

    fun GeneratedDbSource.makeDriverInstance(): String {
        return when (pluginOptions.platformType) {
            KotlinPlatformType.androidJvm -> {
                "AndroidSqliteDriver(${name}.Schema, ctx, name)"
            }
            KotlinPlatformType.native -> {
                "NativeSqliteDriver(${name}.Schema, name)"
            }
            else -> {
                error("${pluginOptions.platformType} target's DB driver is not yet supported")
            }
        }
    }
}



