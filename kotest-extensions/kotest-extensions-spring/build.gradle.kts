plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      val jvmMain by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(kotlin("reflect"))
            implementation(libs.spring.context)
            implementation(libs.spring.test)
            implementation(libs.byte.buddy)
         }
      }
      val jvmTest by getting {
         dependencies {
            implementation(libs.spring.boot.test)
         }
      }
   }
}
