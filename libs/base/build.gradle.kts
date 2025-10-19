plugins {
    alias(libs.plugins.convention.android.lib)
    alias(libs.plugins.convention.kmp)
    alias(libs.plugins.convention.serialization)
    alias(libs.plugins.convention.koin)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.multiplatform.settings)
            }
        }
    }
}

android { namespace = "ru.krirll" }
