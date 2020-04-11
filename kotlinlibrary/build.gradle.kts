plugins {
    id("java")
    id("kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}


dependencies {
    api(deps.kotlin.stdlibWithVersion)
    api(deps.kotlin.stdlibJdk8)
    api(deps.ktor.clientSerializationJvm)
    api(deps.ktor.clientGson)
    api(deps.ktor.clientLoggingJvm)
    api(deps.kotlin.serializationRuntime)
    api(deps.kotlin.coroutineCore)
    api(deps.kotlin.reflect)
    api(deps.ktor.clientCoreJvm)
    api(deps.gson)
}

publish(false, false, "jvm", "JVM")