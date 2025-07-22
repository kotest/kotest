plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      jvmMain {
         dependencies {
            implementation(libs.ksp)
            implementation(libs.kotlin.poet)
         }
      }
   }
}
