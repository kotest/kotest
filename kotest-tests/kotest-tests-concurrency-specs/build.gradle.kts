plugins {
   id("kotest-jvm-conventions")
}

kotlin {

   sourceSets {

      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.Assertions.Core))
         }
      }
   }
}
