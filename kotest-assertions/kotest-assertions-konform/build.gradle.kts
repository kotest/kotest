plugins {
   id("java")
   id("kotlin-multiplatform")
   id("java-library")
}

repositories {
   mavenCentral()
   maven(url = "https://dl.bintray.com/konform-kt/konform")
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
   }

   targets.all {
      compilations.all {
         kotlinOptions {
            freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
         }
      }
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(Libs.Konform.Konform)
            implementation(project(Projects.AssertionsShared))
            implementation(project(Projects.AssertionsCore))
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(Libs.Konform.KonformJvm)
         }
      }

      val jsMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(Libs.Konform.KonformJs)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
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
      showStandardStreams = true
      events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
         org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED)
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
   }
}

apply(from = "../../publish-mpp.gradle.kts")
