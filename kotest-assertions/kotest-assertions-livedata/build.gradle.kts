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

      getByName("androidMain") {
         dependencies {
            implementation(project(Projects.AssertionsShared))
            implementation(project(Projects.AssertionsApi))
            implementation(project(Projects.extension("livedata")))
            implementation(AndroidLibs.AndroidX.coreKtx)
            implementation(AndroidLibs.AndroidX.Lifecycle.runtimeKtx)
            implementation(AndroidLibs.AndroidX.Lifecycle.liveDataKtx)
            implementation(AndroidLibs.AndroidX.Lifecycle.viewModelKtx)
            implementation(AndroidLibs.AndroidX.ArchCore.testing)
         }
      }

      getByName("androidTest") {
         dependencies {
            implementation(project(Projects.Engine))
            implementation(project(Projects.JunitRunner))
            implementation("org.assertj:assertj-core:3.16.1")
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
