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
      js {
         val main by compilations.getting {
            kotlinOptions {
               moduleKind = "commonjs"
            }
         }
      }
      when {
         Ci.os.isMacOsX -> macosX64("native")
         Ci.os.isWindows -> mingwX64("native")
         else -> linuxX64("native")
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

      val commonMain by getting {
         dependencies {
            implementation(kotlin("stdlib-common"))
            implementation(Libs.Coroutines.coreCommon)
            implementation(project(":kotest-mpp"))
            api(project(":kotest-assertions"))
         }
      }

      val jsMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(kotlin("stdlib-js"))
            implementation(Libs.Coroutines.coreJs)
            implementation(Libs.JUnitPlatform.commons)
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(kotlin("stdlib-jdk8"))
            implementation(kotlin("reflect"))
            implementation(Libs.Coroutines.core)
            implementation(Libs.Coroutines.jdk8)
            implementation("com.github.wumpz:diffutils:2.2")
            implementation("com.univocity:univocity-parsers:2.8.4")
            implementation("com.github.mifmif:generex:1.0.2")
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(":kotest-runner:kotest-runner-junit5"))
            implementation(project(":kotest-property"))
            implementation(Libs.OpenTest4j.core)
         }
      }

      val nativeMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(Libs.Coroutines.coreNative)
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
