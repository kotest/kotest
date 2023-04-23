plugins {
   id("kotest-jvm-conventions")
}

kotlin {

   sourceSets {

      jvmTest {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

   }
}
