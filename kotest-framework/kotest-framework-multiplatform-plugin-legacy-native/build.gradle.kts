plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
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
