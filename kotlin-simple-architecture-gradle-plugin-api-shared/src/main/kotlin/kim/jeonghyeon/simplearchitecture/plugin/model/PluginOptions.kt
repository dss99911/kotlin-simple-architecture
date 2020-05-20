package kim.jeonghyeon.simplearchitecture.plugin.model

data class PluginOptions(
    val sourceSets: List<SourceSetOption>,
    val buildPath: String,
    val projectPath: String,
    val compileTargetVariantsName: String
) {
    fun hasCommon() = sourceSets.any { it.isCommon() }
}