package kim.jeonghyeon.simplearchitecture.plugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project

val Project.androidExtension get() = project.extensions.findByType(BaseExtension::class.java)
val Project.hasAndroid get() = androidExtension != null