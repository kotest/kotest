plugins {
   `kotlin-dsl`
}

dependencies {
   implementation(libs.testlogger.gradle.plugin)
   implementation(libs.kotlin.gradle.plugin)
}

kotlin {
   compilerOptions {
      allWarningsAsErrors = true
   }
}
