plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(libs.kotlin.reflect)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(libs.jdom2)
         }
      }
   }
}
