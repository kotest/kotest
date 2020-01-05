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
            api(project(":kotest-assertions"))
            api(kotlin("stdlib-common"))
            api(Libs.Coroutines.coreCommon)
         }
      }

      val jsMain by getting {
         dependsOn(commonMain)
         dependencies {
            api(kotlin("stdlib-js"))
            api(kotlin("test-js"))
            api(Libs.Coroutines.coreJs)
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            api(kotlin("stdlib-jdk8"))
            implementation(kotlin("reflect"))
            api(Libs.Coroutines.core)
            implementation(Libs.Slf4j.api)
            implementation(Libs.JUnitJupiter.api)
            implementation("io.arrow-kt:arrow-core:0.10.3")

         }
      }
   }
}

apply(from = "../publish.gradle")
