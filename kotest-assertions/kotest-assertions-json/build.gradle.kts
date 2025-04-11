plugins {
   id("kotest-multiplatform-library-conventions")
   alias(libs.plugins.kotlin.serialization)
}

kotlin {

   sourceSets {

      val commonMain by getting {
         dependencies {
            compileOnly(libs.jetbrainsAnnotations)
            implementation(projects.kotestCommon)
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.kotestAssertions.kotestAssertionsShared)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestProperty)
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(libs.jayway.json.path)
         }
      }

      // compileOnly is not supported in these source-sets, so needs must expose it as api too
      named(listOf("nativeMain", "jsMain", "wasmJsMain")::contains).configureEach {
         dependencies { api(libs.jetbrainsAnnotations) }
      }
   }
}
