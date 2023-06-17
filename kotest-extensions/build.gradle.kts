plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {

   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(projects.kotestFramework.kotestFrameworkApi)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestCommon)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.mockk)
         }
      }
   }
}

tasks.withType<Test>().configureEach {
   jvmArgs("--add-opens=java.base/java.util=ALL-UNNAMED")
}
