plugins {
    alias(libs.plugins.convention.android.lib)
    alias(libs.plugins.convention.kmp)
    alias(libs.plugins.convention.serialization)
    alias(libs.plugins.convention.koin)
    alias(libs.plugins.convention.compose)
}

android { namespace = "ru.krirll.ui" }
