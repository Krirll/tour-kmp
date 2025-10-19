plugins {
    alias(libs.plugins.convention.android.app)
    kotlin("android")
    alias(libs.plugins.convention.compose)
    alias(libs.plugins.convention.koin)
}

android {
    compileOptions { isCoreLibraryDesugaringEnabled = true }
    buildFeatures { buildConfig = true }
    namespace = "ru.krirll.moscowtour.multiplatform"
    defaultConfig {
        applicationId = namespace
        minSdk = libs.versions.proj.min.get().toInt()
        targetSdk = libs.versions.proj.target.get().toInt()
        versionCode = libs.versions.proj.version.code.get().toInt()
        versionName = libs.versions.proj.version.name.get()
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.activity.compose)
    implementation(project(":moscow-tour:client:shared"))
    implementation(project(":libs:base"))
    implementation(project(":libs:ui"))
    implementation(project(":libs:http-client"))
    debugImplementation(libs.ui.tooling)
    compileOnly(libs.ui.tooling.preview.android)
    testImplementation(libs.koin.test)
    testImplementation(libs.mockk)
    testImplementation(kotlin("test-junit"))
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.kotlin.reflect)
    androidTestImplementation(libs.runner)
}
