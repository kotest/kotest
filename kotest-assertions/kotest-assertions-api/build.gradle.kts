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
         languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
         languageSettings.useExperimentalAnnotation("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}

apply(from = "../../publish-mpp.gradle.kts")
