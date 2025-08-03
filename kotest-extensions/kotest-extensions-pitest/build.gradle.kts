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
            compileOnly(libs.pitest)
         }
      }
      jvmTest {
         dependencies {
            implementation(libs.pitest)
         }
      }
   }
}
