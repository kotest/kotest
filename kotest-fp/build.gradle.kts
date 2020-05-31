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

      linuxX64()
      mingwX64()
      macosX64()
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

      listOf("macosX64Main", "linuxX64Main", "mingwX64Main").forEach {
         get(it).dependencies {
            implementation(Libs.Coroutines.coreNative)
         }
      }
   }
}

apply(from = "../publish-mpp.gradle.kts")
