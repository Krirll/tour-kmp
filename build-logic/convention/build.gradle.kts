plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.plugins.android.library.toDep())
    compileOnly(libs.plugins.android.application.toDep())
    compileOnly(libs.plugins.compose.compiler.toDep())
    compileOnly(libs.plugins.jetbrains.compose.toDep())
    compileOnly(libs.plugins.serialization.toDep())
    compileOnly(libs.plugins.kotlin.multiplatform.toDep())
}

fun Provider<PluginDependency>.toDep() = map {
    "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}

val prefix = "ru.krirll.plugin."

gradlePlugin {
    plugins {
        create("conventionKmp") {
            id = "convention.kmp"
            implementationClass = "${prefix}Kmp"
        }
        create("conventionKtorClient") {
            id = "convention.ktor-client"
            implementationClass = "${prefix}KtorClient"
        }
        create("conventionCompose") {
            id = "convention.compose"
            implementationClass = "${prefix}Compose"
        }
        create("conventionSerialization") {
            id = "convention.serialization"
            implementationClass = "${prefix}Serialization"
        }
        create("conventionAndroidLib") {
            id = "convention.android-lib"
            implementationClass = "${prefix}AndroidLibrary"
        }
        create("conventionSqlDelight") {
            id = "convention.sqldelight"
            implementationClass = "${prefix}SqlDelight"
        }
        create("conventionJvm") {
            id = "convention.jvm"
            implementationClass = "${prefix}Jvm"
        }
        create("conventionKoin") {
            id = "convention.koin"
            implementationClass = "${prefix}Koin"
        }
        create("conventionAndroidApp") {
            id = "convention.android-app"
            implementationClass = "${prefix}AndroidApplication"
        }
    }
}
