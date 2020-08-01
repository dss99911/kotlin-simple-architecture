buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }

    dependencies {
        classpath(deps.plugin.gradlePublish)//gradle plugin publishing
    }
}

//todo adding common dependency here was not working.
/*
subprojects {
    dependencies {
        add("compileOnly", deps.plugin.auto)
        add("kapt", deps.plugin.auto)
    }
}

configure(subprojects.filter { it.name != deps.simpleArch.pluginShared.getArtifactId()}) {
    dependencies {
        add("implementation", deps.simpleArch.pluginShared)
    }
}
 */