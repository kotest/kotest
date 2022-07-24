@Suppress("DSL_SCOPE_VIOLATION")
plugins {
   id("kotest-multiplatform-library-conventions")
   alias(libs.plugins.kotlin.serialization)
}

kotlin {

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(project(Projects.Common))
            implementation(libs.kotlinx.serialization.json)
            implementation(project(Projects.Assertions.Shared))
            implementation(project(Projects.Assertions.Core))
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(project(Projects.Framework.api))
            implementation(project(Projects.Framework.datatest))
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Property))
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(libs.jayway.json.path)
         }
      }
   }
}
