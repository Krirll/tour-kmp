package ru.krirll.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import ru.krirll.androidImplementation
import ru.krirll.findLibs
import ru.krirll.hasAndroidSupport
import ru.krirll.implementation

class SqlDelight : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        val libs = findLibs()
        plugins.apply(libs.findPlugin("sqldelight").get().get().pluginId)
        dependencies {
            implementation(libs.findLibrary("coroutines.extensions").get())
            implementation(libs.findLibrary("sql.delight.runtime").get())

            if (hasAndroidSupport()) {
                androidImplementation(libs.findLibrary("androidx.sqlite").get())
                androidImplementation(libs.findLibrary("androidx.sqlite.framework").get())
            }
        }
    }
}
