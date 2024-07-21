rootProject.name = "test-project"

pluginManagement {
   val kotlinVersion: String by settings
   val kotestGradlePluginVersion: String by settings
   val devMavenRepoPath: String by settings

   plugins {
      id("org.jetbrains.kotlin.multiplatform") version kotlinVersion
      id("io.kotest.multiplatform") version kotestGradlePluginVersion
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

buildCache {
   val kotestUser = providers.gradleProperty("Kotest_GradleBuildCache_user").orNull
   val kotestPass = providers.gradleProperty("Kotest_GradleBuildCache_pass").orNull
   remote<HttpBuildCache> {
      url = uri("https://kotest-gradle.duckdns.org/cache")
      credentials {
         username = kotestUser
         password = kotestPass
      }
      isPush = kotestUser != null && kotestPass != null
   }
   local {
      // Disable local cache when running on GitHub Actions to reduce the size of GitHub Actions cache,
      // and to ensure that CI builds updates the remote cache.
      val isCI = System.getenv("CI") == "true"
      isEnabled = !isCI
   }
}
