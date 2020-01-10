plugins {
   id("java")
   id("kotlin-multiplatform")
   id("java-library")
}

repositories {
   mavenCentral()
}

kotlin {

   targets {
      jvm {
         targets {
            jvm {
               compilations.all {
                  kotlinOptions {
                     jvmTarget = "1.8"
                  }
               }
            }
         }
      }
   }

   targets.all {
      compilations.all {
         kotlinOptions {
            freeCompilerArgs + "-Xuse-experimental=kotlin.Experimental"
         }
      }
   }

   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(kotlin("stdlib-jdk8"))
            api(kotlin("reflect"))
            api(project(":kotest-fp"))
            api(project(":kotest-core"))
            api(project(":kotest-extensions"))
            implementation(Libs.Coroutines.core)
            implementation(Libs.Classgraph.classgraph)
            implementation(Libs.Slf4j.api)
            api("io.arrow-kt:arrow-core:0.10.3")
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(":kotest-runner:kotest-runner-junit5"))
         }
      }
   }
}

tasks {
   test {
      useJUnitPlatform()
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
}

apply(from = "../../publish.gradle")
