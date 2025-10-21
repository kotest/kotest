import org.jetbrains.kotlin.gradle.tasks.KotlinTest

plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-publishing-conventions")
}

kotlin {

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.konform)
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
         }
      }

      val jvmTest by getting {
         dependencies {
         }
      }
   }
}

tasks.withType<KotlinTest>().configureEach {
   failOnNoDiscoveredTests = false
}
