plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(project(Projects.Common))
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Framework.api))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.junit.jupiter.api)
         }
      }
   }
}
