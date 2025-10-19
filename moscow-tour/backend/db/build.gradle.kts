plugins {
    alias(libs.plugins.convention.jvm)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.convention.koin)
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("ru.krirll.moscowtour.backend")
            dialect("app.cash.sqldelight:postgresql-dialect:2.0.2")
        }
    }
}

dependencies {
    implementation(project(":moscow-tour:common"))
    implementation(project(":libs:backend-base"))
    implementation(project(":libs:base"))
    implementation(project(":libs:http-client"))

    implementation(libs.jdbc.driver)
    implementation(libs.postgresql)
}
