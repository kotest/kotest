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
            implementation(project(Projects.Common))
            implementation(project(Projects.AssertionsShared))
         }
      }

      val jsMain by getting {
         dependsOn(commonMain)
         dependencies {
            // this must be api as it's compiled into the final source
            api(kotlin("test-js"))
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(kotlin("reflect"))
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(project(Projects.Engine))
            implementation(project(Projects.AssertionsCore))
            // we use the internals of the JVM project in the tests
            implementation(project(Projects.JunitRunner))
            implementation(Libs.Coroutines.coreJvm)
            implementation(Libs.Mocking.mockk)
            implementation(Libs.JUnitPlatform.engine)
            implementation(Libs.JUnitPlatform.api)
            implementation(Libs.JUnitPlatform.launcher)
            implementation(Libs.JUnitJupiter.api)
            // this is here to test that the intellij marker 'dummy' test doesn't appear in intellij
            implementation(Libs.JUnitJupiter.engine)
         }
      }
   }
}

tasks.named("compileKotlinJs") {
   this as org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
   kotlinOptions.moduleKind = "umd"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
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
