package ru.krirll.plugin

import com.android.build.gradle.TestedExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import ru.krirll.findLibs
import ru.krirll.setupAndroid

class AndroidLibrary : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        val libs = findLibs()
        pluginManager.apply(libs.findPlugin("android.library").get().get().pluginId)
        extensions.configure<TestedExtension> { setupAndroid(target) }
    }
}
