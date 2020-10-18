import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
   kotlin("multiplatform")
   id("com.android.library")
   id("com.adarshr.test-logger")
}

repositories {
   jcenter()
}

android {
   compileSdkVersion(30)
   defaultConfig {
      minSdkVersion(19)
      targetSdkVersion(30)
   }

   compileOptions {
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
   }

   testOptions {
      unitTests.isReturnDefaultValues = true
   }
}

kotlin {
   android {
      compilations.all {
         kotlinOptions {
            jvmTarget = "1.8"
         }
      }
   }
   sourceSets {
      targets.all {
         compilations.all {
            kotlinOptions {
               freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
            }
         }
      }

      val androidMain by getting {
         dependencies {
            implementation(project(Projects.Api))
            implementation(AndroidLibs.AndroidX.coreKtx)
            implementation(AndroidLibs.AndroidX.Lifecycle.liveDataKtx)
            implementation(AndroidLibs.AndroidX.ArchCore.testing)
         }
      }

      val androidTest by getting {
         dependencies {
            implementation(project(Projects.JunitRunner))
         }
      }
   }
}

tasks.withType<Test> {
   useJUnitPlatform()
   filter {
      isFailOnNoMatchingTests = false
   }
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(
         TestLogEvent.FAILED,
         TestLogEvent.PASSED
      )
      exceptionFormat = TestExceptionFormat.FULL
   }
}

//apply(from = "../../publish-mpp.gradle.kts")
