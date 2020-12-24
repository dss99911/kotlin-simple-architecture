package kim.jeonghyeon.simplearchitecture.plugin

import kim.jeonghyeon.simplearchitecture.plugin.generator.ApiGenerator
import kim.jeonghyeon.simplearchitecture.plugin.generator.DbGenerator
import kim.jeonghyeon.simplearchitecture.plugin.model.PluginOptions
import kim.jeonghyeon.simplearchitecture.plugin.model.SharedKtFile
import java.io.File

object SourceCodeGenerator {
    fun generate(pluginOptions: PluginOptions, origin: Collection<SharedKtFile>): Collection<File> {
//        if (pluginOptions.compileTargetVariantsName.startsWith("metadata")) {
        if (pluginOptions.compileTargetVariantsName.endsWith("test", true)) {
            //metadata, common both are built. ignore meta data => when compile metadata, error occurs as file not exists.
            //todo need to analyze detail, when publish release, error occurs. publishing local is working fine.
            //todo this seems not required when publish one more time. it's working without this code also
            return emptyList()
        }

        val apiFiles = ApiGenerator(pluginOptions, origin).generate()
        val dbFiles = DbGenerator(pluginOptions, origin).generate()
        return apiFiles + dbFiles
    }
}