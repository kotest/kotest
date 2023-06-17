plugins {
   `kotlin-dsl`
}

repositories {
   mavenCentral()
   gradlePluginPortal()
}

kotlin {
   jvmToolchain(17)
}

dependencies {
   implementation(libs.testlogger.gradle.plugin)
   implementation(libs.kotlin.gradle.plugin)
}
