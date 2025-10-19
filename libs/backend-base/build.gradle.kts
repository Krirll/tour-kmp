plugins {
    alias(libs.plugins.convention.jvm)
    alias(libs.plugins.convention.koin)
}
dependencies {
    implementation(libs.java.jwt)
    implementation(libs.logback.classic)
}
