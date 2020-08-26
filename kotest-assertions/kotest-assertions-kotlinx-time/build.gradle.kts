plugins {
   kotlin("multiplatform")
   id("com.adarshr.test-logger")
}

repositories {
   mavenCentral()
   maven(url = "https://kotlin.bintray.com/kotlinx/")
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
            implementation(project(Projects.Common))
            implementation(Libs.Coroutines.coreCommon)
            implementation(Libs.KotlinTime.kotlintime)
            // this is api because we want to expose `shouldBe` etc
            api(project(Projects.AssertionsShared))
         }
      }

      val jvmTest by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
         }
      }
   }
}

tasks.named<Test>("jvmTest") {
   useJUnitPlatform()
   filter {
      isFailOnNoMatchingTests = false
   }
   testLogging {
      showExceptions = true
      events = setOf(
         org.gradle.api.tasks.testing.logging.TestLogEvent.STARTED,
         org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
         org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
         org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
         org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
         org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
      )
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
   }
}

apply(from = "../../publish-mpp.gradle.kts")
