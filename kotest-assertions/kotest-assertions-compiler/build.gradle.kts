plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      val jvmMain by getting {
         dependencies {
            implementation(libs.kotlin.compiler.embeddable)
            implementation(libs.kotlin.compile.testing)
         }
      }
   }
}
