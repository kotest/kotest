plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      jvmMain {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(libs.kotlin.reflect)
            implementation(libs.spring.context)
            implementation(libs.spring.test)
            implementation(libs.byte.buddy)
         }
      }
      jvmTest {
         dependencies {
            implementation(libs.spring.boot.starter.web)
            implementation(libs.spring.boot.starter.test)
         }
      }
   }
}
