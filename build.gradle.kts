plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    id("io.gitlab.arturbosch.detekt") version "1.23.5"
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

group = "org.bytebloom"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.github.jan-tennert.supabase:supabase-kt:3.8.0")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.ktor.client.cio)
    implementation(libs.kotlinx.serialization.json)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}