plugins {
   id("java")
   kotlin("multiplatform")
   kotlin("plugin.serialization")
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
      js(IR) {
         browser()
         nodejs()
      }
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(Libs.Serialization.json)
            implementation(project(Projects.AssertionsShared))
            implementation(Libs.Jayway.jsonpath)
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(project(Projects.AssertionsCore))
            implementation(project(Projects.Api))
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Property))
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(Libs.Jayway.jsonpath)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
         }
      }

      all {
         languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
         languageSettings.useExperimentalAnnotation("kotlin.experimental.ExperimentalTypeInference")
         languageSettings.useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
      }
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
