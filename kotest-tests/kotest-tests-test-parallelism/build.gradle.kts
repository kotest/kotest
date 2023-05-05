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
            implementation(projects.kotestRunner.kotestRunnerJunit5)
            implementation(libs.kotlinx.coroutines.core)
         }
      }
   }
}
