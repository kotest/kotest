@Suppress("DSL_SCOPE_VIOLATION")
plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
   alias(libs.plugins.kotlinBinaryCompatibilityValidator)
}

kotlin {
   sourceSets {

      jvmMain {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestFramework.kotestFrameworkApi)
            implementation(libs.jdom2)
         }
      }
   }
}
