plugins {
   id("java")
   id("kotlin-multiplatform")
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
               freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
            }
         }
      }

      val commonMain by getting {
         dependencies {
            implementation(kotlin("stdlib-common"))
            implementation(Libs.Klock.klock)
            implementation(project(":kotest-assertions"))
         }
      }

      val jvmTest by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(project(":kotest-runner:kotest-runner-junit5"))
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
      events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED, org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED)
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
   }
}

apply(from = "../../publish.gradle")
