rootProject.name = "buildSrc"

pluginManagement {
   repositories {
      mavenCentral()
      gradlePluginPortal()
      maven("https://maven.google.com")
   }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
   repositoriesMode = RepositoriesMode.PREFER_SETTINGS
   repositories {
      mavenCentral()
      gradlePluginPortal()
      maven("https://maven.google.com")
   }
   versionCatalogs {
      create("libs") {
         from(files("../gradle/libs.versions.toml"))
      }
   }
}
