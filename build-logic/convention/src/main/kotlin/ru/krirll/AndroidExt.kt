package ru.krirll

import com.android.build.gradle.TestedExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

fun TestedExtension.setupAndroid(project: Project) {
    val libs = project.findLibs()
    compileSdkVersion = "android-" + libs.findVersion("proj.compile").get().toString()
    defaultConfig {
        minSdk = libs.findVersion("proj.min").get().toString().toInt()
        targetSdkVersion(libs.findVersion("proj.target").get().toString().toInt())
    }
    compileOptions {
        val javaVersion = JavaVersion.toVersion(libs.findVersion("proj.java").get())
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}
