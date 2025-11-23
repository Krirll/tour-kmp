plugins {
    alias(libs.plugins.convention.android.lib)
    alias(libs.plugins.convention.kmp)
    alias(libs.plugins.convention.ktor.client)
    alias(libs.plugins.convention.compose)
    alias(libs.plugins.convention.serialization)
    alias(libs.plugins.convention.sqldelight)
    alias(libs.plugins.convention.koin)
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("ru.krirll.moscowtour.app")
            generateAsync.set(true)
        }
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":moscow-tour:common"))
            implementation(libs.coil.compose)
            implementation(project(":libs:base"))
            implementation(project(":libs:ui"))
            implementation(project(":libs:http-client"))
            implementation(libs.kotlinx.datetime)
        }
        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val wasmJsMain by getting {
            dependencies {
                implementation(libs.sqldelight.web.driver)
                implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.1.0"))
                implementation(npm("sql.js", "1.10.3")) // или актуальную 1.x
                implementation(devNpm("copy-webpack-plugin", "11.0.0"))
            }
        }
    }
}

android {
    namespace = "ru.krirll.moscowtour.shared"
}
