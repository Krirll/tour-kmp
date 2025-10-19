plugins {
    alias(libs.plugins.convention.android.lib)
    alias(libs.plugins.convention.kmp)
    alias(libs.plugins.convention.serialization)
    alias(libs.plugins.convention.koin)
    alias(libs.plugins.convention.ktor.client)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":libs:base"))
                implementation(libs.ktor.client.cio)
            }
        }
    }
}

android { namespace = "ru.krirll.http" }
