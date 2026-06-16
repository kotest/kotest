plugins {
   id("kotest-jvm-conventions")
   id("kotest-js-conventions")
   id("kotest-wasi-conventions")
   id("kotest-native-conventions")
   id("kotest-android-native-conventions")
   id("kotest-watchos-device-conventions")
   id("kotest-publishing-conventions")
   id("com.google.devtools.ksp").version("2.3.9")
   id("io.kotest").version("6.1.11")
}

// The infix assertions, e.g. collection shouldContain element. These delegate to the
// matcher implementations in kotest-assertions-core-logic.
kotlin {

   sourceSets {

      commonMain {
         dependencies {
            api(projects.kotestAssertions.kotestAssertionsCoreLogic)

            implementation(libs.kotlin.reflect)
            implementation(projects.kotestCommon)
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }

      jvmMain {
         dependencies {
            implementation(libs.kotlinx.coroutines.jdk8)
            implementation(libs.diffutils)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestProperty)
            implementation(projects.kotestAssertions.kotestAssertionsTable)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.apache.commons.lang)
            implementation(libs.mockk)
            implementation(libs.jimfs)
         }
      }
   }
}
