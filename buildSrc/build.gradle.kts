plugins {
   `kotlin-dsl`
}

repositories {
   mavenCentral()
   gradlePluginPortal()
}

dependencies {
   implementation(libs.testlogger.gradle.plugin)
   implementation(libs.kotlin.gradle.plugin)
}
