plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-android-native-conventions")
}

kotlin {
   sourceSets {
      commonMain {
         dependencies {
            implementation(projects.kotestProperty)
            implementation(libs.kotlinx.datetime)
         }
      }
      commonTest {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
         }
      }
      jvmTest {
         dependencies {
            implementation(projects.kotestRunner.kotestRunnerJunit5)
         }
      }
   }
}
