plugins {
   `kotlin-dsl`
}

dependencies {
   implementation(libs.kotlin.gradle.plugin)
   implementation(libs.kotest.gradle.plugin)
   implementation(libs.ksp.gradle.plugin)
   implementation(libs.android.kotlin.multiplatform.library)
   implementation(libs.devPublish.plugin)
   implementation(libs.nmcp.plugin)
}

tasks.withType<AbstractArchiveTask>().configureEach {
   // https://docs.gradle.org/current/userguide/working_with_files.html#sec:reproducible_archives
   isPreserveFileTimestamps = false
   isReproducibleFileOrder = true
}
