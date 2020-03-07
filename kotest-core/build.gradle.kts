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
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
         }
      }
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(project(":kotest-mpp"))
            api(project(":kotest-assertions"))
            // this seems to need to be API otherwise it won't find it in projects that depend on core
            api(project(":kotest-fp"))
            api(kotlin("stdlib-common"))
            implementation(Libs.Coroutines.coreCommon)
         }
      }

      val jsMain by getting {
         dependsOn(commonMain)
         dependencies {
            api(kotlin("stdlib-js"))
            api(kotlin("test-js"))
            implementation(Libs.Coroutines.coreJs)
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            api(kotlin("stdlib-jdk8"))
            implementation(kotlin("reflect"))
            implementation(Libs.Coroutines.core)
            api(Libs.JUnitJupiter.api)
            implementation(Libs.Classgraph.classgraph)
         }
      }
   }
}

apply(from = "../publish-mpp.gradle.kts")
