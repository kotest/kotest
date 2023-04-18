plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      jvmMain {
         dependencies {
            compileOnly(libs.kotlin.compiler)
         }
      }
   }
}
