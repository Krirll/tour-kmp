plugins {
    alias(libs.plugins.convention.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.serialization)
    alias(libs.plugins.convention.koin)
}

dependencies {
    implementation(project(":moscow-tour:common"))
    implementation(project(":moscow-tour:backend:db"))
    implementation(project(":libs:backend-base"))
    implementation(project(":libs:base"))
    implementation(project(":libs:http-client"))

    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.host.common.jvm)
    implementation(libs.ktor.server.status.pages.jvm)
    implementation(libs.ktor.server.content.negotiation.jvm)
    implementation(libs.ktor.serialization.kotlinx.json.jvm)
    implementation(libs.ktor.server.netty.jvm)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.websockets.jvm)
    implementation(libs.ktor.server.cors)

    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.kxml2)
    implementation(libs.jsoup)
    testImplementation(libs.koin.test)
    testImplementation(libs.mockk)
    testImplementation(libs.junit)

    implementation(libs.poi.ooxml)
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.proj.server.java.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.proj.server.java.get())
}

group = "ru.krirll.moscowtour"
version = "1.0"

val mainClassName = "ru.krirll.moscowtour.backend.MainKt"

application {
    mainClass.set(mainClassName)

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

tasks {
    register("fatJar", Jar::class) {
        mustRunAfter(jarTask("db"), ":libs:backend-base:jar")

        archiveBaseName = "server-fat"
        manifest {
            attributes["Implementation-Title"] = "MoscowTour Server"
            attributes["Implementation-Version"] = version
            attributes["Main-Class"] = mainClassName
        }
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.WARN
        with(jar.get() as CopySpec)
    }

    "build" {
        dependsOn("fatJar")
    }

    register<Exec>("deployRemote") {
        dependsOn("fatJar")
        commandLine(
            getCommandLine(
                "docker context use remote",
                "docker build . -t moscowtour-server",
                "docker context use default"
            )
        )
        notCompatibleWithConfigurationCache("This task uses Exec which is not compatible with configuration cache")
    }

    register<Exec>("deployLocal") {
        dependsOn("fatJar")
        commandLine(
            getCommandLine(
                "docker context use default",
                "docker compose down",
                "docker compose up -d"
            )
        )
        notCompatibleWithConfigurationCache("This task uses Exec which is not compatible with configuration cache")
    }
}

tasks.matching { it.name in listOf("distZip", "distTar", "installDist") }.configureEach {
    enabled = false
}

fun jarTask(name: String): String {
    return ":moscow-tour:backend:$name:jar"
}

private fun getCommandLine(vararg commands: String): List<String> {
    val osName = System.getProperty("os.name").lowercase()
    return if (osName.contains("win")) {
        listOf("cmd", "/c") + commands.joinToString(" && ")
    } else {
        listOf("sh", "-c") + commands.joinToString(" && ")
    }
}
