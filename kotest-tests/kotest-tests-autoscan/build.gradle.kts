plugins {
   id("kotest-jvm-conventions")
}

kotlin {

   sourceSets {

      val jvmTest by getting {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }

   }
}
