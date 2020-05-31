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

      when {
         Ci.os.isMacOsX -> macosX64()
         Ci.os.isWindows -> mingwX64()
         else -> linuxX64()
      }
   }

   targets.all {
      compilations.all {
         kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + "-Xuse-experimental=kotlin.Experimental"
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

      val sourceSet = when {
         Ci.os.isMacOsX -> get("macosX64Main")
         Ci.os.isWindows -> get("mingwX64Main")
         else -> get("linuxX64Main")
      }

      sourceSet.dependencies {
         implementation(Libs.Coroutines.coreNative)
      }
   }
}

apply(from = "../publish-mpp.gradle.kts")
