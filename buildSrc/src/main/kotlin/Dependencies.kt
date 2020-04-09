import KotlinDependencies.coroutineTest

val deps = Dependencies

object Dependencies {
    val kotlin = KotlinDependencies
    val ktor = KtorDependencies
    val android = AndroidDependencies
    val simpleArch = SimpleArchitecture
    val bintrary = BintraryDependencies
    val shadowGradle = "com.github.jengelman.gradle.plugins:shadow:${versions.shadow}"
    val gson = "com.google.code.gson:gson:2.8.6"
}

object KotlinDependencies {
    val gradle = depKotlin("gradle-plugin", versions.kotlin.version)
    val serializationGradle = depKotlin("serialization", versions.kotlin.version)
    val stdlib = depKotlin("stdlib")
    val stdlibWithVersion = depKotlin("stdlib", versions.kotlin.version)
    val stdlibJdk8 = depKotlin("stdlib-jdk8", versions.kotlin.version)
    val coroutineCoreCommon = depKotlinx("coroutines-core-common", versions.kotlin.coroutine)
    val coroutineCore = depKotlinx("coroutines-core", versions.kotlin.coroutine)
    val coroutineAndroid = depKotlinx("coroutines-android", versions.kotlin.coroutine)
    val coroutineCoreNative = depKotlinx("coroutines-core-native", versions.kotlin.coroutine)
    val coroutineTest = depKotlinx("coroutines-test", versions.kotlin.coroutine)
    val serializationRuntimeCommon = depKotlinx("serialization-runtime-common", versions.kotlin.serialization)
    val serializationRuntime = depKotlinx("serialization-runtime", versions.kotlin.serialization)
    val serializationRuntimeNative = depKotlinx("serialization-runtime", versions.kotlin.serialization)
    val reflect = depKotlin("reflect", versions.kotlin.version)
}

object KtorDependencies {
    val clientCore = "io.ktor:ktor-client-core:${versions.kotlin.ktor}"
    val clientCoreJvm = "io.ktor:ktor-client-core-jvm:${versions.kotlin.ktor}"
    val clientIos = "io.ktor:ktor-client-ios:${versions.kotlin.ktor}"
    val clientAndroid = "io.ktor:ktor-client-android:${versions.kotlin.ktor}"
    val clientSerialization = "io.ktor:ktor-client-serialization:${versions.kotlin.ktor}"
    val clientSerializationJvm = "io.ktor:ktor-client-serialization-jvm:${versions.kotlin.ktor}"
    val clientSerializationNative = "io.ktor:ktor-client-serialization-native:${versions.kotlin.ktor}"
}

object AndroidDependencies {
    val buildToolGradle = "com.android.tools.build:gradle:${versions.android.buildTool}"
    val navigationGradle = "androidx.navigation:navigation-safe-args-gradle-plugin:${versions.android.xUi}"
    val supportCompat = "com.android.support:support-compat:28.0.0"
    val appCompat = "androidx.appcompat:appcompat:${versions.android.xBase}"
    val core = "androidx.core:core-ktx:${versions.android.xBase}"
    val vectordrawable = "androidx.vectordrawable:vectordrawable:${versions.android.xBase}"
    val recyclerView = "androidx.recyclerview:recyclerview:1.1.0"
    val material = "com.google.android.material:material:${versions.android.material}"
    val preference = "androidx.preference:preference:1.1.0"
    val fragment = "androidx.fragment:fragment-ktx:${versions.android.fragment}"
    val fragmentTesting = "androidx.fragment:fragment-testing:${versions.android.fragment}"
    val viewPager = "androidx.viewpager2:viewpager2:1.0.0"
    val work = "androidx.work:work-runtime-ktx:2.3.1"
    val paging = "androidx.paging:paging-runtime-ktx:2.1.1"
    val constraintlayout = "androidx.constraintlayout:constraintlayout:${versions.android.constraintLayout}"
    val lifecycle = listOf(
        "androidx.lifecycle:lifecycle-extensions:${versions.android.xUi}",
        "androidx.lifecycle:lifecycle-livedata-ktx:${versions.android.xUi}",
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${versions.android.xUi}",
        "androidx.lifecycle:lifecycle-common-java8:${versions.android.xUi}",
        "androidx.lifecycle:lifecycle-viewmodel-savedstate:${versions.android.xUi}"
    )
    val navigation = listOf(
        "androidx.navigation:navigation-fragment-ktx:${versions.android.xUi}",
        "androidx.navigation:navigation-ui-ktx:${versions.android.xUi}"
    )
    val room = listOf(
        "androidx.room:room-runtime:${versions.android.room}",
        "androidx.room:room-ktx:${versions.android.room}"
    )
    val coin = listOf(
        "org.koin:koin-core:${versions.koin}",
        "org.koin:koin-android:${versions.koin}",
        "org.koin:koin-androidx-scope:${versions.koin}",
        "org.koin:koin-androidx-viewmodel:${versions.koin}"
    )
    val picasso = "com.squareup.picasso:picasso:2.71828"
    val anko = "org.jetbrains.anko:anko:0.10.8"
    val timber = "com.jakewharton.timber:timber:4.7.1"

    val testCommon = listOf(
        "androidx.arch.core:core-testing:2.1.0",
        "androidx.test.ext:junit-ktx:1.1.1",
        "androidx.test:core-ktx:${versions.android.xTest}",
        "androidx.test:core:${versions.android.xTest}",
        "androidx.test:rules:${versions.android.xTest}",
        "androidx.test:runner:${versions.android.xTest}",
        "org.mockito:mockito-core:3.2.4",
        coroutineTest,
        "androidx.room:room-testing:${versions.android.room}",
        "com.google.truth:truth:1.0.1"
    )
}

object BintraryDependencies {
    val androidMavenGradle = "com.github.dcendents:android-maven-gradle-plugin:2.0"
    val gradle = "com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.0"
}

object SimpleArchitecture {
    val jvm = depSimpleArchitecture("jvm", PublishConfig.versionLibrary)
    val android = depSimpleArchitecture("android", PublishConfig.versionLibrary)
    val androidTest = depSimpleArchitecture("android-test", PublishConfig.versionTest)
    val androidAndroidTest = depSimpleArchitecture("android-androidtest", PublishConfig.versionTest)
}

private fun depKotlin(module: String, version: String? = null): String =
    "org.jetbrains.kotlin:kotlin-$module${version?.let { ":$version" } ?: ""}"

private fun depKotlinx(module: String, version: String? = null): String =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"

private fun depSimpleArchitecture(module: String, version: String? = null): String =
    "kim.jeonghyeon:kotlin-simple-architecture-$module${version?.let { ":$version" } ?: ""}"