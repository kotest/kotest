plugins {
   `kotlin-dsl`
}

dependencies {
   implementation(libs.testlogger.gradle.plugin)
   implementation(libs.kotlin.gradle.plugin)
   implementation("io.kotest:kotest-framework-multiplatform-plugin-gradle:5.9.1")
}
