plugins {
   id("kotest-jvm-conventions")
}

kotlin {

   sourceSets {
      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Assertions.Shared))
         }
      }
   }
}
