object versions {
    object kotlin {
        const val version = "1.3.72"
        const val coroutine = "1.3.5"
        const val ktor = "1.3.2"
        const val serialization = "0.12.0"

        //todo this can't be used. after the issue below is fixed. it can be used
        // https://github.com/Kotlin/kotlinx.atomicfu/issues/90
        const val atomicfu = "0.14.3"
    }

    object android {
        const val buildTool = "4.2.0-alpha01"
        const val xBase = "1.1.0"
        const val xUi = "2.2.0"
        const val xTest = "1.2.0"
        const val xEspresso = "3.2.0"
        const val fragment = "1.2.0"
        const val room = "2.2.3"
        const val material = "1.1.0"
        const val constraintLayout = "2.0.0-beta3"
        const val compose = "0.1.0-dev14"
    }

    const val koin = "2.1.6"
    const val shadow = "5.0.0"
    const val sqldelight = "1.4.0"
}