plugins {
   id("kotest-jvm-conventions")
}

kotlin {
   sourceSets {
      val jvmMain by getting {
         dependencies {
            compileOnly(libs.kotlin.compiler)
         }
      }
   }
}
