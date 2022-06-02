plugins {
   id("kotlin-conventions")
}

kotlin {
   targets {
      jvm {
         compilations.all {
            kotlinOptions {
               jvmTarget = "1.8"
            }
         }
      }
   }

   sourceSets {
      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.JunitRunner))
         }
      }
   }
}
