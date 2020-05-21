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

      linuxX64()
      mingwX64()
      macosX64()

      if (Ci.ideaActive) {
         when {
            Ci.os.isMacOsX -> macosX64("native")
            Ci.os.isWindows -> mingwX64("native")
            Ci.os.isLinux -> linuxX64("native")
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

      val nativeMain = if (Ci.ideaActive) get("nativeMain") else create("nativeMain")

      listOf("macosX64Main", "linuxX64Main", "mingwX64Main").forEach {
         val sourceSet = get(it)
         sourceSet.dependsOn(nativeMain)
         sourceSet.dependencies {
            implementation(Libs.Coroutines.coreNative)
         }
      }
   }
}

apply(from = "../publish-mpp.gradle.kts")
