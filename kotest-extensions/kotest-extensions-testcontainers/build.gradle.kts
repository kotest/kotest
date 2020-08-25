plugins {
   id("java")
   kotlin("multiplatform")
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
   }

   targets.all {
      compilations.all {
         kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
         }
      }
   }

   sourceSets {
      val jvmMain by getting {
         dependencies {
            implementation(project(Projects.Engine))
            implementation(project(Projects.Api))
            api(Libs.TestContainers.testcontainers)
            api(Libs.Coroutines.coreJvm)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
            implementation(project(Projects.AssertionsCore))
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
