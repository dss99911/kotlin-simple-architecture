plugins {
    kotlin("multiplatform")
    application
}

group = "me.user"
version = "1.0"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
        withJava()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
            }
        }

        val jvmMain by getting {
            dependencies {
            }
        }
    }
}

application {
    mainClassName = "TempTestKt"
}