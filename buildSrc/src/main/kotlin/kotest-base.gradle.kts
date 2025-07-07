plugins {
   base
}

extensions.create(KotestBuildLogicSettings.EXTENSION_NAME, KotestBuildLogicSettings::class)

tasks.withType<AbstractArchiveTask>().configureEach {
   // https://docs.gradle.org/current/userguide/working_with_files.html#sec:reproducible_archives
   isPreserveFileTimestamps = false
   isReproducibleFileOrder = true
}

tasks.withType<Test>().configureEach {
   // Register OS-specific variables as task inputs to ensure test results from another OS
   // are not incorrectly loaded from remote Build Cache, which could prevent the tests
   // from detecting OS-specific issues.
   inputs.property("file.separator", providers.systemProperty("file.separator"))
   inputs.property("line.separator", providers.systemProperty("line.separator"))
   inputs.property("path.separator", providers.systemProperty("path.separator"))
}
