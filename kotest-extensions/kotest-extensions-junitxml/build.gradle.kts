plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestFramework.kotestFrameworkApi)
            implementation(libs.jdom2)
         }
      }
   }
}
