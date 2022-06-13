plugins {
   id("kotest-jvm-conventions")
}

kotlin {

   sourceSets {

      val jvmTest by getting {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Assertions.Core))
         }
      }

   }
}
