package ru.krirll.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import ru.krirll.androidImplementation
import ru.krirll.desktopImplementation
import ru.krirll.findLibs
import ru.krirll.implementation

class KtorClient : Plugin<Project> {
    override fun apply(target: Project) {
        val libs = target.findLibs()
        target.dependencies {
            implementation(libs.findLibrary("ktor.client.websockets").get())
            implementation(libs.findLibrary("ktor-serialization-kotlinx-json").get())
            implementation(libs.findLibrary("ktor-client-content-negotiation").get())
            implementation(libs.findLibrary("ktor-client-logging").get())
            implementation(libs.findLibrary("ktor-client-auth").get())
            implementation(libs.findLibrary("ktor-client-core").get())
            val okHttp = libs.findLibrary("ktor.client.okhttp").get()
            try {
                androidImplementation(okHttp)
                desktopImplementation(okHttp)
            } catch (_: Exception) {
                implementation(okHttp)
            }
        }
    }
}
