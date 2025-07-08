plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
   alias(libs.plugins.allure)
}

kotlin {
   sourceSets {
      val jvmMain by getting {
         dependencies {
            implementation(kotlin("reflect", libs.versions.kotlin.get()))
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            api(libs.allure.commons)
         }
      }
      val jvmTest by getting {
         dependencies {
            implementation(libs.jackson.module.kotlin)
         }
      }
   }
}

allure {
   version.set(libs.versions.allure)
   adapter.autoconfigure.set(false)
   adapter.autoconfigureListeners.set(false)
   adapter {
      frameworks {
         junit5.enabled.set(false)
      }
   }
}
