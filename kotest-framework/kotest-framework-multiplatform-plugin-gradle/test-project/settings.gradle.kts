rootProject.name = "test-project"

pluginManagement {
   val kotlinVersion: String by settings

   plugins {
      id("org.jetbrains.kotlin.multiplatform") version kotlinVersion
   }
}

includeBuild("../../../")
