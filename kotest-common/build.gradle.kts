@Suppress("DSL_SCOPE_VIOLATION")
plugins {
   id("kotest-multiplatform-library-conventions")
   alias(libs.plugins.kotlinBinaryCompatibilityValidator)
}

kotlin {
   sourceSets {

      commonMain {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }
   }
}
