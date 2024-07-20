plugins {
   id("kotlin-conventions")
}

kotlin {
   jvm()

   sourceSets {
      val jvmTest by getting {
         dependencies {
            implementation(projects.kotestRunner.kotestRunnerJunit5)
         }
      }
   }
}
