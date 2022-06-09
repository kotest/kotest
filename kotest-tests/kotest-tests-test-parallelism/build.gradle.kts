plugins {
   id("kotlin-conventions")
}

kotlin {

   targets {
      jvm()
   }

   sourceSets {
      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.JunitRunner))
            implementation(libs.kotlinx.coroutines.core)
         }
      }
   }
}
