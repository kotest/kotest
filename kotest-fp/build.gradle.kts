plugins {
   id("java")
   id("kotlin-multiplatform")
   id("java-library")
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
            implementation(kotlin("stdlib-common"))
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
         }
      }

      if (!ideaActive) {
         val nativeMain by creating {
            dependsOn(commonMain)
         }

         configure(listOf(getByName("macosX64Main"), getByName("linuxX64Main"), getByName("mingwX64Main"))) {
            dependsOn(nativeMain)
         }
      }
   }
}

apply(from = "../publish-mpp.gradle.kts")
