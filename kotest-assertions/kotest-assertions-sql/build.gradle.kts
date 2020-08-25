plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")
   id("com.adarshr.test-logger")
}

repositories {
   jcenter()
}

kotlin {
   sourceSets {

      targets {
         jvm {
            compilations.all {
               kotlinOptions {
                  jvmTarget = "1.8"
               }
            }
         }
      }

      targets.all {
         compilations.all {
            kotlinOptions {
               freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
            }
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(project(Projects.AssertionsShared))
            implementation(project(Projects.AssertionsCore))
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
            implementation(Libs.Mocking.mockk)
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
      showStandardStreams = true
      events = setOf(
         org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
         org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
      )
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
   }
}

apply(from = "../../publish-mpp.gradle.kts")
