package kim.jeonghyeon.simplearchitecture.plugin.model

data class GeneratedDbSource(
    val name: String,//db interface name
    val packageName: String,//db interface packageName
    val sourceSetPath: String
)