rootProject.name = "test-project"

pluginManagement {
   val kotlinVersion: String by settings
   val kotestVersion: String by settings
   val devMavenRepoPath: String by settings

   plugins {
      id("org.jetbrains.kotlin.multiplatform") version kotlinVersion
      id("io.kotest.multiplatform") version kotestVersion
   }

   repositories {
      maven(file(devMavenRepoPath)) {
         name = "DevMavenRepo"
         mavenContent { includeGroupAndSubgroups("io.kotest") }
      }
      mavenCentral()
      gradlePluginPortal()
   }
}
