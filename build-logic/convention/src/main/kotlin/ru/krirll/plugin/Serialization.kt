package ru.krirll.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import ru.krirll.findLibs
import ru.krirll.implementation

class Serialization : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        val libs = findLibs()
        plugins.apply(libs.findPlugin("serialization").get().get().pluginId)
        dependencies {
            implementation(libs.findLibrary("kotlinx.serialization.json").get())
            implementation(libs.findLibrary("kotlinx.serialization.core").get())
        }
    }
}
