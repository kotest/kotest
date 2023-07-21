plugins {
   `kotlin-dsl`
}

repositories {
   mavenCentral()
   gradlePluginPortal()
}

kotlin {
   jvmToolchain(11)
}

dependencies {
   implementation(libs.testlogger.gradle.plugin)
   implementation(libs.kotlin.gradle.plugin)
}
