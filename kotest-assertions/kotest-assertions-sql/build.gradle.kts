@Suppress("DSL_SCOPE_VIOLATION")
plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
   alias(libs.plugins.kotlinBinaryCompatibilityValidator)
}

kotlin {
   sourceSets {
      jvmMain {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsShared)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

      jvmTest {
         dependencies {
            implementation(libs.mockk)
         }
      }
   }
}
