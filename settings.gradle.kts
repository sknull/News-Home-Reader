rootProject.name = "news-homereader"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

val gprProps = java.util.Properties().apply {
    val file = File(rootDir, "local.properties")
    if (file.exists()) load(file.inputStream())
}

dependencyResolutionManagement {
    repositories {
        google()
//        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/sknull/essence")
            credentials {
                username = "sknull"
                password = System.getenv("PERSONAL_ACCESS_TOKEN") ?: gprProps.getProperty("gpr.key")
            }
            content {
                includeGroup("de.visualdigits")
            }
        }
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space")
        maven("https://central.sonatype.com/repository/maven-snapshots/")
        maven("https://jitpack.io")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":composeApp")
includeBuild("../Stephans-KMP-Components") {
    dependencySubstitution {
        substitute(module("de.visualdigits.kmp:stephans-kmp-components"))
            .using(project(":library"))
    }
}
