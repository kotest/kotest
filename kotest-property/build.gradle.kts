plugins {
   id("kotest-multiplatform-library-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(kotlin("reflect"))
            api(project(Projects.Common))
            api(project(Projects.Assertions.Shared))
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      val jvmMain by getting {
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

      val desktopTest by creating {
         dependsOn(commonTest)
         dependencies {
            implementation(kotlin("test-common"))
         }
      }

      val iosX64Test by getting {
         dependsOn(desktopTest)
      }

   }
}
