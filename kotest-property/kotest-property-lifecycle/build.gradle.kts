plugins {
   id("kotest-jvm-conventions")
   id("kotest-js-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            api(project(Projects.Property))
            api(project(Projects.Framework.api))
            implementation(libs.kotlinx.coroutines.core)
            implementation(project(Projects.Common))
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Assertions.Core))
         }
      }
   }
}
