plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(kotlin("reflect"))

            api(projects.kotestCommon)
            api(projects.kotestFramework.kotestFrameworkEngine)
            api(projects.kotestFramework.kotestFrameworkDiscovery)

            // used to write to the console with fancy colours!
            api(libs.mordant)
         }
      }
   }
}
