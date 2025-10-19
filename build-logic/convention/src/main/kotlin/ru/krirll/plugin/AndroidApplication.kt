package ru.krirll.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import ru.krirll.findLibs
import ru.krirll.setupAndroid
import java.io.File
import java.io.FileInputStream
import java.util.Properties

class AndroidApplication : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = findLibs()
        pluginManager.apply(libs.findPlugin("android.application").get().get().pluginId)
        extensions.configure<AppExtension> {
            val keystorePropertiesFile = File(rootDir, "keystore.properties")
            signingConfigs {
                if (keystorePropertiesFile.exists()) {
                    val keystoreProperties = Properties()
                    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
                    create("release") {
                        storeFile = File(rootDir, "release.keystore")
                        check(storeFile?.exists() == true) { "Unable to load keystore file!" }
                        storePassword = keystoreProperties["RELEASE_STORE_PASSWORD"].toString()
                        keyAlias = keystoreProperties["RELEASE_KEY_ALIAS"].toString()
                        keyPassword = keystoreProperties["RELEASE_KEY_PASSWORD"].toString()
                    }
                }
            }
            buildTypes {
                getByName("release") {
                    isMinifyEnabled = true
                    if (keystorePropertiesFile.exists()) {
                        signingConfig = signingConfigs.getByName("release")
                    }
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        file("proguard-rules.pro")
                    )
                }
                getByName("debug") {
                    isMinifyEnabled = false
                    applicationIdSuffix = ".debug"
                    if (keystorePropertiesFile.exists()) {
                        signingConfig = signingConfigs.getByName("release")
                    }
                }
            }
            setupAndroid(target)
        }
    }
}
