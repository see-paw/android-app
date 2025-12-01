// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.dagger.hilt.android") version "2.57.2" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

tasks.register<Exec>("runBackend") {
    workingDir = file("../backend")
    commandLine("dotnet", "run", "--project", "WebApi")
}

detekt {
    config = files("$rootDir/config/detekt/detekt.yml")
    buildUponDefaultConfig = true
    allRules = false
}