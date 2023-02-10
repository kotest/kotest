plugins {
   id("kotest-jvm-conventions")
   `kotest-publishing-test`

   idea
}

description = "Android integration tests"

kotlin {
   sourceSets {
      val jvmTest by getting {
         dependencies {
            implementation(projects.kotestCommon)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(project.dependencies.gradleTestKit())
         }
      }
   }
}

dependencies {
   testMavenPublication(projects.kotestCommon)
   testMavenPublication(projects.kotestFramework.kotestFrameworkEngine)
   testMavenPublication(projects.kotestAssertions.kotestAssertionsApi)
   testMavenPublication(projects.kotestAssertions.kotestAssertionsShared)
   testMavenPublication(projects.kotestAssertions.kotestAssertionsCore)
   testMavenPublication(projects.kotestAssertions.kotestAssertionsJson)
}

val updateAndroidLibraryGradleProperties by tasks.registering(WriteProperties::class) {
   comment = " Manual edits will be overwritten by $path"
   properties(
      "org.gradle.jvmargs" to "-Xmx2048m -Dfile.encoding=UTF-8",
      "android.useAndroidX" to "true",
      "android.nonTransitiveRClass" to "true",
      "android.enableJetifier" to "false",
      "kotestPublishVersion" to Ci.publishVersion,
      "kotestTestMavenRepoDir" to kotestPublishingTest.testMavenRepoDir.get().asFile.absolutePath,
   )
   outputFile = file("$projectDir/android-library/gradle.properties")
}

val updateAndroidLibraryLocalProperties by tasks.registering(WriteProperties::class) {
   comment = """
      multi
      line
      comment
   """.trimIndent()
   properties(
      "sdk.dir" to projectDir.resolve("/android-library/ANDROID_SDK").absolutePath,
   )
   outputFile = file("$projectDir/android-library/local.properties")
}

tasks.withType<Test>().configureEach {
   dependsOn(configurations.testMavenPublication)
   dependsOn(
      updateAndroidLibraryGradleProperties,
      updateAndroidLibraryLocalProperties,
   )

   val androidLibraryProjectDir = file("android-library")
   outputs.dir(androidLibraryProjectDir)

   systemProperty("androidLibraryProjectDir", androidLibraryProjectDir.absolutePath)
   systemProperty("kotestTestMavenRepoDir", kotestPublishingTest.testMavenRepoDir.get().asFile.absolutePath)
}


idea {
   module {
      // for some reason IntelliJ doesn't automatically exclude these directories from search results,
      // so manually add them
      excludeDirs.plusAssign(
         listOf(
            file("android-library/.gradle"),
            file("android-library/.idea"),
            file("android-library/build"),
            file("android-library/ANDROID_SDK"),
         )
      )
   }
}
