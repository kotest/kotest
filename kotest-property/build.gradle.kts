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
         val main by compilations.getting {
            kotlinOptions {
               // Setup the Kotlin compiler options for the 'main' compilation:
               jvmTarget = "1.8"
            }

            compileKotlinTask // get the Kotlin task 'compileKotlinJvm'
            output // get the main compilation output
         }
      }
      js {
         val main by compilations.getting {
            kotlinOptions {
               moduleKind = "commonjs"
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

      val commonMain by getting {
         dependencies {
            implementation(kotlin("stdlib-common"))
            api(project(":kotest-assertions"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.3")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
         }
      }

      val jsMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(kotlin("stdlib-js"))
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(kotlin("stdlib-jdk8"))
            implementation(kotlin("reflect"))
            implementation("com.github.wumpz:diffutils:2.2")
            implementation("com.univocity:univocity-parsers:2.8.3")
            api("io.arrow-kt:arrow-core:0.10.3")

            implementation("com.github.mifmif:generex:1.0.2")
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

//jvmTest {
//   useJUnitPlatform()
//
//   // show standard out and standard error of the test JVM(s) on the console
//   testLogging.showStandardStreams = true
//
//   // Always run tests, even when nothing changed.
//   dependsOn 'cleanTest'
//
//   testLogging {
//      events "FAILED", "SKIPPED", "STANDARD_OUT", "STANDARD_ERROR"
//      exceptionFormat = 'full'
//   }
//}

apply(from = "../publish.gradle")
