plugins {
   id("kotest-jvm-conventions")
}

kotlin {

   sourceSets {

      jvmTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }
   }
}
