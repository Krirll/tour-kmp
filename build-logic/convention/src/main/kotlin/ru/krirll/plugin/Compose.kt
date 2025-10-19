package ru.krirll.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.compose.ComposePlugin
import ru.krirll.findLibs
import ru.krirll.implementation

class Compose : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        val libs = findLibs()
        plugins.apply(libs.findPlugin("jetbrains.compose").get().get().pluginId)
        plugins.apply(libs.findPlugin("compose.compiler").get().get().pluginId)
        dependencies {
            val compose = ComposePlugin.Dependencies(target)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(libs.findLibrary("decompose").get())
            implementation(libs.findLibrary("decompose-extensions").get())
            implementation(libs.findLibrary("decompose-lifecycle-coroutines").get())
            implementation(libs.findLibrary("image.loader").get())
        }
    }
}
