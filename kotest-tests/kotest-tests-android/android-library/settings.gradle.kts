rootProject.name = "android-library"

pluginManagement {
   repositories {
      google()
      mavenCentral()
      gradlePluginPortal()
   }
}

//val kotestTestMavenRepoDir by project

dependencyResolutionManagement {
   repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
   repositories {
      google()
      mavenCentral()
      maven(providers.gradleProperty("kotestTestMavenRepoDir")) {
         name = "android-test-maven-repo"
      }
   }
}
