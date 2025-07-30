plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      jvmMain {
         dependencies {
            api(projects.kotestFramework.kotestFrameworkEngine)
            api(libs.junit4)
            api(libs.kotlinx.coroutines.core)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.junit.platform.testkit)
         }
      }
   }
}
