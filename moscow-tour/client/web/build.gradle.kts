@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.convention.kmp)
    alias(libs.plugins.convention.compose)
    alias(libs.plugins.convention.koin)
}

kotlin {

    sourceSets {
        val wasmJsMain by getting {
            dependencies {
                implementation(project(":moscow-tour:client:shared"))
                implementation(project(":moscow-tour:common"))
                implementation(project(":libs:base"))
                implementation(project(":libs:ui"))
            }
        }
    }

    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }
}
