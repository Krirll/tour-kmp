package ru.krirll.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import ru.krirll.findLibs
import ru.krirll.implementation

class Jvm : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        val libs = findLibs()
        plugins.apply(libs.findPlugin("java-library").get().get().pluginId)
        plugins.apply(libs.findPlugin("jetbrains-kotlin-jvm").get().get().pluginId)

        tasks.withType<JavaCompile> {
            sourceCompatibility = libs.findVersion("proj-server-java").get().toString()
            targetCompatibility = libs.findVersion("proj-server-java").get().toString()
        }

        dependencies {
            implementation(libs.findLibrary("kotlinx-coroutines-core").get())
        }
    }
}

internal fun DependencyHandlerScope.ksp(deps: Any) {
    try {
        add("kspCommonMainMetadata", deps)
        add("kspAndroid", deps)
    } catch (_: Exception) {
        add("ksp", deps)
    }
}
