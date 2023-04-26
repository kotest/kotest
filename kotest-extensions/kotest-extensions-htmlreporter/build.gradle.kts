plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      jvmMain {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestFramework.kotestFrameworkApi)
            implementation(libs.jdom2)
         }
      }
   }
}
