plugins {
   `kotlin-dsl`
}

repositories {
   gradlePluginPortal()
}

dependencies {
   implementation(libs.testlogger.gradle.plugin)
   implementation(libs.kotlin.gradle.plugin)
}
