package ru.krirll.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import ru.krirll.androidImplementation
import ru.krirll.findLibs
import ru.krirll.hasAndroidSupport
import ru.krirll.implementation

class Kmp : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        val libs = findLibs()
        pluginManager.apply(libs.findPlugin("kotlin-multiplatform").get().get().pluginId)
        val hasAndroid = hasAndroidSupport()
        extensions.configure<KotlinMultiplatformExtension> { apply(hasAndroid) }
        dependencies {
            implementation(libs.findLibrary("kotlinx-coroutines-core").get())
            if (hasAndroid) {
                androidImplementation(libs.findLibrary("kotlinx.coroutines.android").get())
            }
        }
        kotlinExtension.sourceSets.named("commonMain").configure {
            kotlin.srcDirs(
                "build/generated/ksp/metadata/commonMain/kotlin"
            )
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    private fun KotlinMultiplatformExtension.apply(hasAndroid: Boolean) {
        if (hasAndroid) {
            androidTarget()
        }

        wasmJs {
            browser()
        }

        jvm("desktop")
    }
}
