plugins {
   id("kotest-jvm-conventions")
   id("kotest-js-conventions")
   id("kotest-native-conventions")
   id("kotest-publishing-conventions")
}

kotlin {

   sourceSets {

      commonMain {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(libs.koin.core)
            implementation(projects.kotestProperty)
            implementation(libs.koin.test.get().let { "${it.module}:${it.versionConstraint.requiredVersion}" }) {
               exclude(group = "junit", module = "junit")
            }
         }
      }

      // koin is exposing kotlin-test as a dependency, and if the versions don't align, the compiler complains,
      // so we need to force it to be same as our version
      commonMain {
         dependencies {
            api("org.jetbrains.kotlin:kotlin-test:${libs.versions.kotlin.asProvider().get()}")
         }
      }

      jvmMain {
         dependencies {
            implementation(projects.kotestRunner.kotestRunnerJunit5)
            implementation(libs.mockk)
         }
      }
   }
}
