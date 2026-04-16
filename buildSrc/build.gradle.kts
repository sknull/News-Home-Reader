plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version "2.3.10"
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("io.github.pdvrieze.xmlutil:core:1.0.0-rc2")
    implementation("io.github.pdvrieze.xmlutil:serialization:1.0.0-rc2")
    implementation("com.deepl.api:deepl-java:1.14.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
    implementation("co.touchlab:kermit:2.1.0")
    implementation("com.jsoizo:kotlin-csv-jvm:1.10.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform() // Wichtig für JUnit 5!
}
