val config = Configuration()

data class Configuration(
    val includeAndroid: Boolean = true,
    val includeBackend: Boolean = true,
    val useLeakCanary: Boolean = false,
    val compileSdkVersion: Int = 28,
    val targetSdkVersion: Int = 28,
    val minSdkVersion: Int = 23,
    val buildToolVersion: String = "28.0.3"
)