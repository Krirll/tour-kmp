pluginManagement {
    includeBuild("build-logic")

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io") // maven repo where the current library resides
        }
    }
}

rootProject.name = "MoscowTour"

include(":moscow-tour:common")

include(":moscow-tour:client:androidApp")
include(":moscow-tour:client:shared")
include(":moscow-tour:client:web")
include(":moscow-tour:backend:core")
include(":moscow-tour:backend:db")

include(":libs:backend-base")
include(":libs:base")
include(":libs:ui")
include(":libs:http-client")
