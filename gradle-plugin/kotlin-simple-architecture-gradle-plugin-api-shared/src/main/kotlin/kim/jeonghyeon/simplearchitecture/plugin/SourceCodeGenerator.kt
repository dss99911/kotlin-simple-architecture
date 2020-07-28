package kim.jeonghyeon.simplearchitecture.plugin

import kim.jeonghyeon.simplearchitecture.plugin.generator.ApiGenerator
import kim.jeonghyeon.simplearchitecture.plugin.generator.DbGenerator
import kim.jeonghyeon.simplearchitecture.plugin.model.PluginOptions
import kim.jeonghyeon.simplearchitecture.plugin.model.SharedKtFile
import java.io.File

object SourceCodeGenerator {
    fun generate(pluginOptions: PluginOptions, origin: Collection<SharedKtFile>): Collection<File> {
        val apiFiles = ApiGenerator(pluginOptions, origin).generate()
        val dbFiles = DbGenerator(pluginOptions, origin).generate()
        return apiFiles + dbFiles
    }
}