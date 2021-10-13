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
      js(BOTH) {
         browser()
         nodejs()
      }

      linuxX64()

      mingwX64()

      macosX64()
      macosArm64()

      tvos()
      tvosSimulatorArm64()

      watchosArm32()
      watchosArm64()
      watchosX86()
      watchosX64()
      watchosSimulatorArm64()

      iosX64()
      iosArm64()
      iosArm32()
      iosSimulatorArm64()
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            implementation(kotlin("reflect"))
            implementation(Libs.Coroutines.coreCommon)
            implementation(project(Projects.Common))
            implementation(project(Projects.AssertionsApi))
            // this is api because we want to expose `shouldBe` etc
            api(project(Projects.AssertionsShared))
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(Libs.Coroutines.jdk8)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.Property))
            implementation(project(Projects.JunitRunner))
            implementation(Libs.OpenTest4j.opentest4j)
            implementation(Libs.Apache.commonslang)
         }
      }

      all {

         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
         languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
      }
   }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions.jvmTarget = "1.8"
   kotlinOptions.apiVersion = "1.6"
}

tasks.named<Test>("jvmTest") {
   useJUnitPlatform()
   filter {
      isFailOnNoMatchingTests = false
   }
   testLogging {
      showExceptions = true
      events = setOf(
         org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
         org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
         org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
      )
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
   }
}

apply(from = "../../publish-mpp.gradle.kts")
