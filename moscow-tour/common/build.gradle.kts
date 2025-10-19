plugins {
    alias(libs.plugins.convention.android.lib)
    alias(libs.plugins.convention.kmp)
    alias(libs.plugins.convention.ktor.client)
    alias(libs.plugins.convention.compose)
    alias(libs.plugins.convention.serialization)
    alias(libs.plugins.convention.koin)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":libs:base"))
                implementation(project(":libs:http-client"))
            }
        }
        val androidMain by getting
    }
}

android {
    namespace = "ru.krirll.moscowtour.common"
}
