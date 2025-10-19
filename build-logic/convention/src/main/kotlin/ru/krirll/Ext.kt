package ru.krirll

import com.android.build.gradle.TestedExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.UnknownConfigurationException
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.getByType

internal fun Project.findLibs(): VersionCatalog {
    return extensions.findByType(VersionCatalogsExtension::class.java)!!.named("libs")
}

internal fun Project.hasAndroidSupport(): Boolean {
    return try {
        extensions.getByType<TestedExtension>()
        true
    } catch (_: Throwable) {
        false
    }
}

internal fun Project.hasMultiplatformSupport(): Boolean {
    val plugin = findLibs().findPlugin("kotlin-multiplatform").get().get().pluginId
    return pluginManager.hasPlugin(plugin)
}

internal fun DependencyHandlerScope.implementation(deps: Any) {
    try {
        add("commonMainImplementation", deps)
    } catch (_: UnknownConfigurationException) {
        add("implementation", deps)
    }
}

internal fun DependencyHandlerScope.androidImplementation(deps: Any) {
    add("androidMainImplementation", deps)
}

internal fun DependencyHandlerScope.desktopImplementation(deps: Any) {
    add("desktopMainImplementation", deps)
}
