plugins {
    alias(libs.plugins.convention.android.lib)
    alias(libs.plugins.convention.kmp)
    alias(libs.plugins.convention.serialization)
    alias(libs.plugins.convention.koin)
    alias(libs.plugins.convention.compose)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.haze)
                implementation(libs.haze.materials)
            }
        }
    }
}

android { namespace = "ru.krirll.ui" }
