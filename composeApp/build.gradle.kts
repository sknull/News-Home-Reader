import de.visualdigits.translation.util.TranslationUtil
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.desktop.application.tasks.AbstractJPackageTask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.gradle.pdf)
    alias(libs.plugins.sqlDelight)
    id("com.google.devtools.ksp") version "2.3.6"
    `maven-publish`
}

version = "1.0.0-SNAPSHOT"
val numericVersion = version.toString().split("-").first() // for desktop packaging

buildscript {
    dependencies {
        classpath(libs.proguardGradle)
    }
}

abstract class GenerateVersionTask : DefaultTask() {
    @get:Input
    abstract val appVersion: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val outputFile = outputDirectory.file("AppVersion.kt").get().asFile
        outputFile.parentFile.mkdirs()
        outputFile.writeText("""package de.visualdigits.generated

data class AppVersion(
    val version: String = "${appVersion.get()}",
) : Comparable<AppVersion> {

    val numericParts: List<Int> = version
        .substringBefore("-")
        .split(".")
        .map { v -> v.toInt() }

    override fun compareTo(other: AppVersion): Int {
        var c = numericParts[0].compareTo(other.numericParts[0])
        var index = 1
        while (c == 0 && index < 3) {
            c = numericParts[index].compareTo(other.numericParts[index])
            index++
        }
        if (c == 0 && version.contains("-")) {
            c = -1
        }
        
        return c
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppVersion

        return numericParts == other.numericParts
    }

    override fun hashCode(): Int {
        return numericParts.hashCode()
    }
}""")
    }
}

val generateVersionClass = tasks.register<GenerateVersionTask>("generateVersionClass") {
    notCompatibleWithConfigurationCache("No caching supported.")
    appVersion.set(project.version.toString())
    outputDirectory.set(layout.buildDirectory.dir("generated/version"))
}

kotlin {
    jvm()
    jvmToolchain(21)
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    sqldelight {
        databases {
            create("SettingsDatabase") {
                packageName = "de.visualdigits.newshomereader"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir(generateVersionClass)
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }

        val androidMain by getting {
            dependencies {
                // android
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)

                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)
                implementation(libs.androidx.work)
                implementation(libs.koin.androidx.workmanager)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.sqldelight.android)

                // android tv
                implementation(project.dependencies.platform("androidx.compose:compose-bom:2026.03.00"))
                implementation(libs.androidx.tv.material)
                implementation(libs.androidx.ui.tooling)
                implementation(libs.androidx.ui.tooling.preview)
            }
        }

        commonMain.dependencies {
            implementation(compose.components.resources)

            implementation(libs.bundles.compose)
            implementation(libs.bundles.coil)
            implementation(libs.bundles.ktor)
            implementation(libs.bundles.koin)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.jetbrains.compose.navigation)

            implementation(libs.kotlin.xml.util)
            implementation(libs.kotlin.xml.serialization)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.io.core)

            implementation(libs.kermit)

            implementation(libs.essence)
            implementation(libs.ksoup.core)

            implementation(libs.html.converter)
            implementation(libs.charlex.pdf)

            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqlite.bundled)

            implementation(libs.compose.colorpicker)
            implementation(libs.stephans.kmp.components)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.junit.jupiter.api)
            implementation(libs.junit.jupiter.engine)
            implementation(libs.junit.platform.launcher)
            implementation(libs.koin.test)
        }

        jvmMain.dependencies {
            implementation(libs.flatlaf)
            implementation(compose.desktop.currentOs)
            implementation(libs.skiko.awt.runtime.windows.x64)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.sqldelight.jvm)
            implementation(libs.kotlinx.io.core.jvm)
        }

        jvmTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.junit.jupiter.api)
            implementation(libs.junit.jupiter.engine)
            implementation(libs.junit.platform.launcher)
            implementation(libs.koin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

configurations.all {
    exclude(group = "ch.qos.logback", module = "logback-classic")
    exclude(group = "ch.qos.logback", module = "logback-core")
}

tasks.withType<PublishToMavenRepository> {
    dependsOn("assembleDebug", "zip")
}

configurations.all {
    exclude(group = "org.jetbrains.compose.material", module = "material-desktop")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Tar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Zip> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register("showDependencies") {
    doLast {
        configurations.kotlinCompilerClasspath.get()
            .forEach { println("#### ${it.canonicalPath}") }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "de.visualdigits.compose.resources"
}

tasks.asciidoctorPdf {
    notCompatibleWithConfigurationCache("No caching supported.")
    baseDirFollowsSourceFile()
    setSourceDir(rootDir)
    sources {
        include("README.adoc")
    }
    setOutputDir(file(layout.buildDirectory.dir("asciidoc")))
    asciidoctorj {
        attributes(
            mapOf(
                "imagesdir" to rootDir.absolutePath,
                "source-highlighter" to "rouge",
                "icons" to "font",
//                "allow-uri-read" to true
            )
        )
    }
}

val copyPdfToDistribution = tasks.register<Copy>("copyPdfToDistribution",) {
    group = "documentation"
    description = "Copies the asciidoc pdf into the distribution"

    val pdfTask = tasks.asciidoctorPdf.get()
    dependsOn(tasks.asciidoctorPdf)
    from(pdfTask.outputDir)
    into(layout.buildDirectory.dir("compose/binaries/main/app/de.visualdigits.newshomereader"))
    include("**/*.pdf")
    eachFile { path = name }
}

val copyPdfToDocs = tasks.register<Copy>("copyPdfToDocs") {
    group = "documentation"
    description = "Copies the asciidoc pdf into the docs directory"

    val pdfTask = tasks.asciidoctorPdf.get()
    dependsOn(tasks.asciidoctorPdf)
    from(pdfTask.outputDir)
    into(file("$rootDir/docs"))
    include("**/*.pdf")
    eachFile { path = name }
}

tasks.matching { it.name == "createDistributable" }.all {
    finalizedBy(copyPdfToDistribution, copyPdfToDocs)
}

tasks.register<Zip>("zip") {
    group = "compose desktop"
    description = "Writes the artifact created by createDistributable to a zip file"

    dependsOn("createDistributable", copyPdfToDistribution, copyPdfToDocs)

    from(layout.buildDirectory.dir("compose/binaries/main/app"))
    from(tasks.asciidoctorPdf.map { it.outputDir }) {
        include("README.pdf")
        into("de.visualdigits.newshomereader")
    }

    archiveFileName.set("NewsHomeReader_${project.version}.zip")
    destinationDirectory.set(layout.buildDirectory.dir("distributions"))
}

tasks.register("extractTranslations") {
    group = "localization"
    description = "Converts the string resources to a csv file under projectroot/translation/stringresources.csv."

    val projectRootDir = project.rootDir
    doLast {
        TranslationUtil.extractTranslation(projectRootDir)
    }
}

tasks.register("updateTranslations") {
    group = "localization"
    description = "Converts the translation csv back to string resources."

    val projectRootDir = project.rootDir
    doLast {
        TranslationUtil.updateTranslation(projectRootDir)
    }
}

tasks.register("joinUpdateTranslations") {
    group = "localization"
    description = "Converts the translation csv back to string resources."

    val projectRootDir = project.rootDir
    doLast {
        TranslationUtil.joinUpdateTranslation(projectRootDir)
    }
}

base {
    archivesName.set("NewsHomeReader")
}

android {
    namespace = "de.visualdigits.newshomereader"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "de.visualdigits.newshomereader"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/io.netty.versions.properties"

            // Schließt ALLE plattformspezifischen Metadaten aus (Native, JS, Wasm)
            excludes += "**/default/linkdata/**"
            excludes += "**/default/manifest"
            excludes += "**/default/module"
//            excludes += "**/*.knm"
//            excludes += "**/*.kotlin_metadata"

            // Speziell für deinen neuen Fehler (JS/Wasm Pfade)
            excludes += "jsAndWasmJsMain/**"
            excludes += "wasmJsMain/**"
            excludes += "jsMain/**"

            pickFirsts.add("META-INF/kotlin-project-structure-metadata.json")
            pickFirsts.add("META-INF/kotlinx-serialization-json.kotlin_module")
            pickFirsts.add("META-INF/resource_loader.kotlin_module")
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

compose.desktop {
    application {
        mainClass = "de.visualdigits.newshomereader.MainKt"

        nativeDistributions {
            packageName = "de.visualdigits.newshomereader"
            packageVersion = numericVersion
            includeAllModules = false
            modules(
                "java.instrument",
                "jdk.unsupported",
                "java.desktop",
                "java.xml",
                "java.naming",
                "java.prefs",
                "java.sql",
                "java.net.http"
            )
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)
            windows {
                upgradeUuid = "5cd976a1-608e-4891-9dc5-20fee6a8c579" // generated by randomness
                shortcut = true
                menu = true
                iconFile.set(project.file("src/commonMain/composeResources/drawable/favicon.ico"))
            }
            linux {
                // Diese Felder sind für .deb oft Pflicht!
                packageName = "news-home-reader"
                debMaintainer = "stephan@visualdigits.de"
                appCategory = "News"
                menuGroup = "Network"
                shortcut = true // Erzeugt den Startmenü-Eintrag
            }
            buildTypes {
                release {
                    proguard {
//                        configurationFiles.from(project.file("proguard-rules.pro"))
                        isEnabled.set(false)
                        optimize.set(false)
                    }
                }
            }
        }
    }
}

configurations.all {
    resolutionStrategy {
        if (name.contains("android", ignoreCase = true)) {
            resolutionStrategy.dependencySubstitution {
                substitute(module("com.fleeksoft.ksoup:ksoup-jvm"))
                    .using(module("com.fleeksoft.ksoup:ksoup:${libs.versions.version.ksoup.get()}"))
            }
        }

        val versionOkHttp = libs.versions.version.okhttp
        val versionOkio = libs.versions.version.okio

//        dependencySubstitution {
//            substitute(module("com.squareup.okhttp3:okhttp"))
//                .using(module("com.squareup.okhttp3:okhttp-jvm:$versionOkHttp"))
//            substitute(module("com.squareup.okio:okio"))
//                .using(module("com.squareup.okio:okio-jvm:$versionOkio"))
//        }
//        force("com.squareup.okhttp3:okhttp-jvm:$versionOkHttp")
//        force("com.squareup.okio:okio-jvm:$versionOkio")

        val versionIo = libs.versions.version.kotlinx.io.core.get()
        force("org.jetbrains.kotlinx:kotlinx-io-core:$versionIo")
        force("org.jetbrains.kotlinx:kotlinx-io-bytestring:$versionIo")
    }
}

publishing {
    publications {
        create<MavenPublication>("binaryRelease") {
            groupId = "de.visualdigits.kmp"
            artifactId = "news-home-reader"
            version = project.version.toString()

            // 1. Android, ZIP, PDF (bleiben wie gehabt)
            artifact(layout.buildDirectory.file("outputs/apk/debug/NewsHomeReader-debug.apk")) {
                extension = "apk"
                classifier = "android"
                builtBy(tasks.matching { it.name == "assembleDebug" })
            }
            artifact(tasks.named<Zip>("zip").flatMap { it.archiveFile }) {
                extension = "zip"
                classifier = "desktop"
            }
            artifact(tasks.named<org.asciidoctor.gradle.jvm.pdf.AsciidoctorPdfTask>("asciidoctorPdf").map { it.outputDir.resolve("README.pdf") }) {
                extension = "pdf"
                classifier = "docs"
            }

            val buildDir = layout.buildDirectory.asFile.get()

            buildDir.walkTopDown().find { it.extension == "deb" }?.let { file ->
                artifact(file) {
                    extension = "deb"
                    classifier = "linux"
                }
            }

            buildDir.walkTopDown().find { it.extension == "msi" }?.let { file ->
                artifact(file) {
                    extension = "msi"
                    classifier = "windows"
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/sknull/News-Home-Reader")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

tasks.withType<PublishToMavenRepository> {
    // Android
    dependsOn(tasks.matching { it.name == "assembleDebug" })
    // Desktop ZIP
    dependsOn(tasks.named("zip"))
    // PDF
    dependsOn(tasks.named("asciidoctorPdf"))

    // Native Installer (nur wenn sie auf dem OS existieren)
    dependsOn(tasks.matching { it.name == "packageDeb" })
    dependsOn(tasks.matching { it.name == "packageMsi" })
    dependsOn(tasks.matching { it.name == "packageReleaseDeb" })
    dependsOn(tasks.matching { it.name == "packageReleaseMsi" })
}
