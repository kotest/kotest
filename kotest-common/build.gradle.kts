plugins {
   id("kotest-multiplatform-library-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            implementation(kotlin("reflect"))
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(project(Projects.Framework.engine))
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
      }

      val desktopMain by creating {
         dependsOn(commonMain)
      }

      val macosX64Main by getting {
         dependsOn(desktopMain)
      }

      val macosArm64Main by getting {
         dependsOn(desktopMain)
      }

      val mingwX64Main by getting {
         dependsOn(desktopMain)
      }

      val linuxX64Main by getting {
         dependsOn(desktopMain)
      }

      val iosX64Main by getting {
         dependsOn(desktopMain)
      }

      val iosArm64Main by getting {
         dependsOn(desktopMain)
      }

      val iosArm32Main by getting {
         dependsOn(desktopMain)
      }

      val iosSimulatorArm64Main by getting {
         dependsOn(desktopMain)
      }

      val watchosArm32Main by getting {
         dependsOn(desktopMain)
      }

      val watchosArm64Main by getting {
         dependsOn(desktopMain)
      }

      val watchosX86Main by getting {
         dependsOn(desktopMain)
      }

      val watchosX64Main by getting {
         dependsOn(desktopMain)
      }

      val watchosSimulatorArm64Main by getting {
         dependsOn(desktopMain)
      }

      val tvosMain by getting {
         dependsOn(desktopMain)
      }

      val tvosSimulatorArm64Main by getting {
         dependsOn(desktopMain)
      }

      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.JunitRunner))
         }
      }

      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
         languageSettings.optIn("kotlin.RequiresOptIn")
      }
   }
}
