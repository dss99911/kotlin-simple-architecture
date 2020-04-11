import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilationToRunnableFiles

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.github.johnrengelman.shadow")
}

kotlin {
    jvm()

    sourceSets {
        val jvmMain by getting {
            kotlin.srcDir("src")
            resources.srcDir("resources")
            languageSettings.apply {
                languageVersion = "1.3"
                apiVersion = "1.3"
            }

            dependencies {
                implementation(project(":common"))


                implementation(deps.ktor.gson)
                implementation(deps.ktor.serverNetty)
                implementation(deps.ktor.serialization)
                implementation(deps.logback)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(deps.kotlin.testJunit)
                implementation(deps.kotlin.test)
                implementation(deps.junit)
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

task<JavaExec>("run") {
    main = "io.ktor.server.netty.EngineMain"
    val jvm by kotlin.targets.getting
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

    archiveBaseName.value("backend")
    classifier = null
    version = null
}
