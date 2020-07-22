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

apply(from = "../publish-mpp.gradle.kts")
