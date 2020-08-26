plugins {
   id("java")
   id("kotlin-multiplatform")
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
         browser()
         nodejs()
      }

      linuxX64()
      linuxArm64()

      mingwX64()
      macosX64()

      tvosX64()
      tvosArm64()

      watchosX86()
      watchosArm64()
      watchosArm32()

      iosX64()
      iosArm64()
      iosArm32()
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
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(kotlin("reflect"))
         }
      }

      val desktopMain by creating {
         dependsOn(commonMain)
      }

      val macosX64Main by getting {
         dependsOn(desktopMain)
      }

      val mingwX64Main by getting {
         dependsOn(desktopMain)
      }

      val linuxX64Main by getting {
         dependsOn(desktopMain)
      }

      val linuxArm64Main by getting {
         dependsOn(desktopMain)
      }

      val iosX64Main by getting {
         dependsOn(desktopMain)
      }

      val iosArm64Main by getting {
         dependsOn(desktopMain)
      }

      val iosArm32Main by getting {
         dependsOn(desktopMain)
      }

      val watchosX86Main by getting {
         dependsOn(desktopMain)
      }

      val watchosArm64Main by getting {
         dependsOn(desktopMain)
      }

      val watchosArm32Main by getting {
         dependsOn(desktopMain)
      }

      val tvosX64Main by getting {
         dependsOn(desktopMain)
      }

      val tvosArm64Main by getting {
         dependsOn(desktopMain)
      }
   }
}

apply(from = "../publish-mpp.gradle.kts")
