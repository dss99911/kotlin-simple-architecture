import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilationToRunnableFiles
import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask

val androidKeyAlias: String by project
val androidKeyPassword: String by project
val androidStoreFile: String by project
val androidStorePassword: String by project

//todo 1.4 remove
val ideaActive = System.getProperty("idea.active") == "true"

plugins {
    //common
    kotlin("multiplatform")

    //android
    //todo when removing sample-android. change to com.android.application
    id("com.android.library")

    //backend
    id("com.github.johnrengelman.shadow")
}

apply(plugin = "kim.jeonghyeon.kotlin-simple-architecture-gradle-plugin")

sqldelight {

    database("SampleDb") {
        packageName = "kim.jeonghyeon.sample"
    }
}


kotlin {
    jvm()//for backend
    android()

    //todo 1.4 remove
    val iosArm32 = iosArm32("iosArm32")
    val iosArm64 = iosArm64("iosArm64")
    val iosX64 = iosX64("iosX64")

    if (ideaActive) {
        iosX64("ios")
    }
    //todo 1.4 remove

    /* todo 1.4
    ios()
    val iosArm32 = iosArm32()
    val iosArm64 = iosArm64()
    val iosX64 = iosX64()

     */

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(deps.simpleArch.common)
            }
        }
        //TODO HYUN [multi-platform2] : consider to change to clientMain. as front end also may be included to here
        val mobileMain by creating {
            dependsOn(commonMain)
        }

        val jvmMain by getting {
            dependencies {
                implementation(deps.ktor.auth)
                implementation(deps.ktor.gson)
                implementation(deps.ktor.serverNetty)
                implementation(deps.ktor.serialization)
                implementation(deps.logback)
                implementation(deps.ktor.serverSessions)
            }
        }

        val androidMain by getting {
            dependsOn(mobileMain)
        }

        val androidDebug by getting {
            dependencies {
                if (config.useLeakCanary) {
                    implementation("com.squareup.leakcanary:leakcanary-android:2.0")
                }
                //    implementation("com.facebook.stetho:stetho:1.5.1")
            }
        }

        //todo 1.4 remove
        val iosMain = if (ideaActive) {
            getByName("iosMain")
        } else {
            create("iosMain")
        }

        iosMain.apply {
            dependsOn(mobileMain)
        }

        val iosArm32Main by getting {}
        val iosArm64Main by getting {}
        val iosX64Main by getting {}

        configure(listOf(iosArm32Main, iosArm64Main, iosX64Main)) {
            dependsOn(iosMain)
        }
        //todo 1.4 remove
        /* todo 1.4
        val iosMain by getting {
            dependsOn(mobileMain)
        }

         */
    }

    val frameworkName = "KotlinApi"

    configure(listOf(iosArm32, iosArm64, iosX64)) {
        compilations {
            val main by getting {
                //for supporting kotlin generic in swift. there were discussion that it'll be applied on kotlin 1.3.40. let's see if this script is required or not.
                kotlinOptions.freeCompilerArgs += "-Xobjc-generics"
            }
        }

        binaries.framework {
            export(deps.kotlin.coroutineCoreCommon)
            export(deps.simpleArch.common)
            //native will use this name to refer the multiplatform library
            baseName = frameworkName
        }
    }

    tasks.register<FatFrameworkTask>("debugFatFramework") {
        baseName = frameworkName
        group = "Universal framework"
        description = "Builds a universal (fat) debug framework"

        from(iosX64.binaries.getFramework("DEBUG"))
    }

    tasks.register<FatFrameworkTask>("releaseFatFramework") {
        baseName = frameworkName
        group = "Universal framework"
        description = "Builds a universal (release) debug framework"

        from(iosArm64.binaries.getFramework("RELEASE"), iosArm32.binaries.getFramework("RELEASE"))
    }
}


android {

    val appId = "kim.jeonghyeon.sample"

    compileSdkVersion(config.compileSdkVersion)
    buildToolsVersion(config.buildToolVersion)
    defaultConfig {
        versionCode = 10000
        versionName = "1.00.00"
        minSdkVersion(config.minSdkVersion)
        targetSdkVersion(config.targetSdkVersion)

        buildConfigField("String", "freePackageName", "\"${appId}\"")

        buildConfigField("boolean", "isFree", "false")
        buildConfigField("boolean", "isPro", "false")
        buildConfigField("boolean", "isDev", "false")
        buildConfigField("boolean", "isProd", "false")
        buildConfigField("boolean", "isMock", "false")
    }

    flavorDimensions("mode", "stage")

    val FLAVOR_NAME_MOCK = "mock"

    productFlavors {
        val free by creating {
            dimension = "mode"
            //todo when removing sampleandroid. uncomment this
            //applicationId = appId
            buildConfigField("boolean", "isFree", "true")
        }

        val pro by creating {
            dimension = "mode"
            //todo when removing sampleandroid. uncomment this
            //applicationId = appId + ".pro"
            buildConfigField("boolean", "isPro", "true")
        }

        val dev by creating {
            dimension = "stage"
            //todo when removing sampleandroid. uncomment this
//            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"

            buildConfigField("boolean", "isDev", "true")
            //optimize build time
            resConfigs("en", "hdpi")
            minSdkVersion(if (config.minSdkVersion > 21) config.minSdkVersion else 21)
        }

        val prod by creating {
            dimension = "stage"
            buildConfigField("boolean", "isProd", "true")
        }

        create(FLAVOR_NAME_MOCK) {
            dimension = "stage"

            //todo when removing sampleandroid. uncomment this
//            applicationIdSuffix = ".mock"
            versionNameSuffix = "-mock"

            buildConfigField("boolean", "isMock", "true")
            //optimize build time
            resConfigs("en", "hdpi")
            minSdkVersion(if (config.minSdkVersion > 21) config.minSdkVersion else 21)
        }
    }

    val SIGNING_CONFIG_NAME_RELEASE = "release"

    signingConfigs {
        create(SIGNING_CONFIG_NAME_RELEASE) {
            keyAlias = androidKeyAlias
            keyPassword = androidKeyPassword
            storeFile = file(androidStoreFile)
            storePassword = androidStorePassword
        }
    }

    val BUILD_TYPE_NAME_DEBUG = "debug"
    val BUILD_TYPE_NAME_RELEASE = "release"

    buildTypes {
        getByName(BUILD_TYPE_NAME_DEBUG) {
            isTestCoverageEnabled = true
        }

        getByName(BUILD_TYPE_NAME_RELEASE) {
            isMinifyEnabled = true
            //todo when removing sampleandroid. uncomment this
//            isShrinkResources = true
            isZipAlignEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName(SIGNING_CONFIG_NAME_RELEASE)
        }
    }

    // Always show the result of every unit test, even if it passes.
    testOptions {
        unitTests.isIncludeAndroidResources = true
        animationsDisabled = true
    }
}

task<JavaExec>("run") {
    main = "io.ktor.server.netty.EngineMain"
    val jvm by kotlin.targets.getting {}
    val main: KotlinCompilation<KotlinCommonOptions> by jvm.compilations

    val runtimeDependencies =
        (main as KotlinCompilationToRunnableFiles<KotlinCommonOptions>).runtimeDependencyFiles
    classpath = files(main.output.allOutputs, runtimeDependencies)
}

tasks.withType<ShadowJar> {
    val jvmJar: Jar by tasks
    val jvmRuntimeClasspath by project.configurations

    configurations = listOf(jvmRuntimeClasspath)

    from(jvmJar.archiveFile)

    archiveBaseName.value("jvm")
    classifier = null
    version = null
}

tasks.register<Exec>("showKeyDetail") {
    commandLine(
        "sh",
        "-c",
        "keytool -list -v -keystore $androidStoreFile -alias $androidKeyAlias -storepass $androidStorePassword -keypass $androidKeyPassword"
    )
}