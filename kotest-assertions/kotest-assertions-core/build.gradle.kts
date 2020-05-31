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

//      linuxX64()
//      mingwX64()
//      macosX64()
//
//      if (Ci.ideaActive) {
//         when {
//            Ci.os.isMacOsX -> macosX64("native")
//            Ci.os.isWindows -> mingwX64("native")
//            Ci.os.isLinux -> linuxX64("native")
//         }
//      }
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
            implementation(project(Projects.Mpp))
            // this is api because we want to expose `shouldBe` etc
            api(project(Projects.AssertionsShared))
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
            implementation(Libs.Coroutines.core)
            implementation(Libs.Coroutines.jdk8)
            implementation(Libs.Wumpz.diffutils)
            implementation("com.univocity:univocity-parsers:2.8.4")
            implementation(Libs.Mifmif.generex)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.Property))
            implementation(project(Projects.JunitRunner))
            implementation(project(Projects.ConsoleRunner))
            implementation(Libs.OpenTest4j.core)
         }
      }

//      val nativeMain = if (Ci.ideaActive) get("nativeMain") else create("nativeMain")
//
//      listOf("macosX64Main", "linuxX64Main", "mingwX64Main").forEach {
//         val sourceSet = get(it)
//         sourceSet.dependsOn(nativeMain)
//         sourceSet.dependencies {
//            implementation(Libs.Coroutines.coreNative)
//         }
//      }
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
