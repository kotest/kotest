plugins {
   id("kotest-jvm-conventions")
}

kotlin {
   sourceSets {
      val jvmMain by getting {
         dependencies {
            implementation(kotlin("stdlib"))
            compileOnly(libs.kotlin.compiler)
         }
      }
   }
}
