plugins {
   id("kotest-jvm-conventions")
}

kotlin {
   sourceSets {
      jvmTest {
         dependencies {
            implementation(projects.kotestRunner.kotestRunnerJunit5)
            implementation(libs.kotlinx.coroutines.core)
         }
      }
   }
}
