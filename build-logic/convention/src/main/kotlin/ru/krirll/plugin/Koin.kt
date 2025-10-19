package ru.krirll.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import ru.krirll.androidImplementation
import ru.krirll.findLibs
import ru.krirll.hasAndroidSupport
import ru.krirll.hasMultiplatformSupport
import ru.krirll.implementation

class Koin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        val libs = findLibs()
        plugins.apply(libs.findPlugin("devtools-ksp").get().get().pluginId)

        dependencies {
            implementation(libs.findLibrary("koin-core").get())
            implementation(libs.findLibrary("kotlinx-coroutines-core").get())
            implementation(libs.findLibrary("koin-annotations").get())
            if (hasAndroidSupport()) {
                val androidKoin = libs.findLibrary("koin-android").get()
                if (hasMultiplatformSupport()) {
                    androidImplementation(androidKoin)
                } else {
                    implementation(androidKoin)
                }
            }

            ksp(libs.findLibrary("koin-ksp-compiler").get())
        }

        if (hasMultiplatformSupport()) {
            tasks.matching { it.name.startsWith("ksp") && it.name != "kspCommonMainKotlinMetadata" }
                .configureEach {
                    dependsOn("kspCommonMainKotlinMetadata")
                }
        }
    }
}
