plugins {
   `kotlin-dsl`
   `embedded-kotlin`
}

repositories {
   mavenCentral()
   gradlePluginPortal()
}

dependencies {
   implementation(libs.testlogger.gradle.plugin)
   implementation(libs.kotlin.gradle.plugin)
   implementation(gradleApi())
}
