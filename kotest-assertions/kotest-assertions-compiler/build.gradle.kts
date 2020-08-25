plugins {
   id("java")
   kotlin("multiplatform")
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
   }

   targets.all {
      compilations.all {
         kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
         }
      }
   }

   sourceSets {
      val jvmMain by getting {
         dependencies {
            implementation(project(Projects.AssertionsShared))
            implementation(Libs.Tschuchortdev.kotlinCompileTesting)
         }
      }
      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
         }
      }
   }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
   kotlinOptions.jvmTarget = "1.8"
   kotlinOptions.apiVersion = "1.3"
}

tasks.named<Test>("jvmTest") {
   useJUnitPlatform()
   filter {
      isFailOnNoMatchingTests = false
   }
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED, org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED)
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
   }
}

apply(from = "../../publish-mpp.gradle.kts")
