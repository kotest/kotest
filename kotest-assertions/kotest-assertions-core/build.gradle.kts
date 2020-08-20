plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")
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
            implementation(Libs.Coroutines.coreCommon)
            implementation(project(Projects.Common))
            implementation(project(Projects.AssertionsApi))
            // this is api because we want to expose `shouldBe` etc
            api(project(Projects.AssertionsShared))
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(kotlin("reflect"))
            implementation(Libs.Coroutines.jdk8)
            implementation(Libs.Wumpz.diffutils)
            implementation("com.univocity:univocity-parsers:2.8.4")
            implementation(Libs.Mifmif.generex)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.Property))
            implementation(project(Projects.JunitRunner))
            implementation(Libs.OpenTest4j.core)
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
