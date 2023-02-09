plugins {
   id("kotest-jvm-conventions")
}

description = "Android integration tests"

kotlin {
   sourceSets {
      val jvmTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestAssertions.kotestAssertionsJson)
            implementation(project.dependencies.gradleTestKit())
         }
      }
   }
}

val syncDependenciesForAndroidLibrary by tasks.registering(Sync::class) {
   description = "Sync dependencies for Android library integration test"
   from(configurations.jvmTestCompileClasspath)
   from(configurations.jvmTestRuntimeClasspath)
   into(layout.buildDirectory.dir("android-test-dependencies"))
   duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}



tasks.withType<Test>().configureEach {
   dependsOn(syncDependenciesForAndroidLibrary)

   val androidLibraryProjectDir = file("android-library")
   outputs.dir(androidLibraryProjectDir)

   systemProperty("androidLibraryProjectDir", androidLibraryProjectDir.absolutePath)

   filter {
      isFailOnNoMatchingTests = false
   }
}
