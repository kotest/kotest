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
      linuxArm64()

      mingwX64()
      mingwX86()

      macosX64()
      macosArm64()

      tvos()

      watchosArm32()
      watchosArm64()
      watchosX86()
      watchosX64()

      iosX64()
      iosArm64()
      iosArm32()
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
         }
      }

      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}

apply(from = "../../publish-mpp.gradle.kts")
