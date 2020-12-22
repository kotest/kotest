plugins {
   id("java")
   id("kotlin-multiplatform")
   id("java-library")
   id("com.adarshr.test-logger")
}

repositories {
   mavenCentral()
}

kotlin {

   targets {
      jvm {
         compilations.all {
            kotlinOptions {
               jvmTarget = "1.8"
            }
         }
      }
      js {
         browser()
         nodejs()
      }

      linuxX64()

      mingwX64()

      macosX64()
      tvos()
      watchos()

      iosX64()
      iosArm64()
      iosArm32()
   }

   targets.all {
      compilations.all {
         kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
         }
      }
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            api(project(Projects.Common))
            api(project(Projects.AssertionsShared))
            implementation(Libs.Coroutines.coreCommon)
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(kotlin("reflect"))
            implementation(Libs.Wumpz.diffutils)
            implementation(Libs.Mifmif.generex)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
            implementation(project(Projects.AssertionsCore))
         }
      }

      val desktopMain by creating {
         dependsOn(commonMain)
      }

      val macosX64Main by getting {
         dependsOn(desktopMain)
      }

      val mingwX64Main by getting {
         dependsOn(desktopMain)
      }

      val linuxX64Main by getting {
         dependsOn(desktopMain)
      }

      val iosX64Main by getting {
         dependsOn(desktopMain)
      }

      val iosArm64Main by getting {
         dependsOn(desktopMain)
      }

      val iosArm32Main by getting {
         dependsOn(desktopMain)
      }

      val watchosMain by getting {
         dependsOn(desktopMain)
      }

      val tvosMain by getting {
         dependsOn(desktopMain)
      }
   }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
   kotlinOptions.jvmTarget = "1.8"
   kotlinOptions.apiVersion = "1.4"
}

tasks.named<Test>("jvmTest") {
   useJUnitPlatform()
   filter {
      isFailOnNoMatchingTests = false
   }
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(
         org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
         org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
      )
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
   }
}

apply(from = "../publish-mpp.gradle.kts")
