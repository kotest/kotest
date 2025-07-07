plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {

   sourceSets {

      jvmMain {
         dependencies {
            implementation(kotlin("reflect", libs.versions.kotlin.get()))
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestCommon)
         }
      }

      jvmTest {
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
