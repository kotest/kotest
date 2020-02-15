plugins {
   id("java")
   id("kotlin-multiplatform")
   id("java-library")
   id("com.adarshr.test-logger")
}

repositories {
   mavenCentral()
}

val ideaActive = System.getProperty("idea.active") == "true"
val os = org.gradle.internal.os.OperatingSystem.current()

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
         val main by compilations.getting {
            kotlinOptions {
               moduleKind = "commonjs"
            }
         }
      }
      if (!ideaActive) {
         linuxX64()
         mingwX64()
         macosX64()
      } else if (os.isMacOsX) {
         macosX64("native")
      } else if (os.isWindows) {
         mingwX64("native")
      } else {
         linuxX64("native")
      }
   }

   targets.all {
      compilations.all {
         kotlinOptions {
            freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
         }
      }
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(project(":kotest-mpp"))
            implementation(kotlin("stdlib-common"))
            api(project(":kotest-assertions"))
            api(project(":kotest-fp"))
            implementation(Libs.Coroutines.coreCommon)
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
            implementation("com.github.wumpz:diffutils:2.2")
            implementation(Libs.Coroutines.core)
            implementation("com.github.mifmif:generex:1.0.2")
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(":kotest-runner:kotest-runner-junit5"))
         }
      }

      if (!ideaActive) {
         val nativeMain by creating {
            dependsOn(commonMain)
            dependencies {
               implementation(Libs.Coroutines.coreNative)
            }
         }

         configure(listOf(getByName("macosX64Main"), getByName("linuxX64Main"), getByName("mingwX64Main"))) {
            dependsOn(nativeMain)
         }
      }
   }
}

tasks.named<Test>("jvmTest") {
   useJUnitPlatform()
   filter {
      setFailOnNoMatchingTests(false)
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
