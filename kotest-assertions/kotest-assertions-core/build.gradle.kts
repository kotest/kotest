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
            implementation(kotlin("stdlib-common"))
            implementation(Libs.Coroutines.coreCommon)
            implementation(project(Projects.Common))
            implementation(Libs.KotlinTime.kotlintime)

            // this is api because we want to expose `shouldBe` etc
            api(project(Projects.AssertionsShared))
         }
      }

      val jsMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(kotlin("stdlib-js"))
            implementation(Libs.Coroutines.coreJs)
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(kotlin("stdlib-jdk8"))
            implementation(kotlin("reflect"))
            implementation(Libs.Coroutines.coreJvm)
            implementation(Libs.Coroutines.jdk8)
            implementation(Libs.Wumpz.diffutils)
            implementation("com.univocity:univocity-parsers:2.8.4")
            implementation(Libs.Mifmif.generex)
         }
      }

      val commonTest by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(project(Projects.Core))
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependsOn(commonTest)
         dependencies {
            implementation(project(Projects.Property))
            implementation(project(Projects.JunitRunner))
            implementation(Libs.OpenTest4j.core)
         }
      }
      


      val macosX64Main by getting {
         dependencies {
            implementation(Libs.Coroutines.coreMacos)
         }
      }

      val mingwX64Main by getting {
         dependencies {
            implementation(Libs.Coroutines.coreMingw)
         }
      }

      val linuxX64Main by getting {
         dependencies {
            implementation(Libs.Coroutines.coreLinux)
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
