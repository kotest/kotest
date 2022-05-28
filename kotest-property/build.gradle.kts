plugins {
   id("kotest-multiplatform-library-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            implementation(kotlin("reflect"))
            api(project(Projects.Common))
            api(project(Projects.Assertions.Shared))
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(libs.diffutils)
            api(libs.rgxgen)
            implementation(kotlin("reflect"))
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Assertions.Core))
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
         }
      }

      val desktopMain by creating {
         dependsOn(commonMain)
      }

      val desktopTest by creating {
         dependsOn(commonTest)
         dependencies {
            implementation(kotlin("test-common"))
         }
      }

      val iosX64Test by getting {
         dependsOn(desktopTest)
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

      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.ExperimentalStdlibApi")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}
