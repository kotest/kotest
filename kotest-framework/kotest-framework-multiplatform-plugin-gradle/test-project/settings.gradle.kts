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


@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
   repositoriesMode = RepositoriesMode.PREFER_SETTINGS

   val devMavenRepoPath: String by settings

   repositories {
      maven(file(devMavenRepoPath)) {
         name = "DevMavenRepo"
         mavenContent { includeGroupAndSubgroups("io.kotest") }
      }
      mavenCentral()
      gradlePluginPortal()

      mavenCentral()
      maven("https://oss.sonatype.org/content/repositories/snapshots/") {
         name = "SonatypeSnapshots"
         mavenContent { snapshotsOnly() }
      }

      //region workaround for https://youtrack.jetbrains.com/issue/KT-51379
      // FIXME remove when updating to Kotlin 2.0
      ivy("https://download.jetbrains.com/kotlin/native/builds") {
         name = "KotlinNative"
         patternLayout {
            listOf(
               "macos-x86_64",
               "macos-aarch64",
               "osx-x86_64",
               "osx-aarch64",
               "linux-x86_64",
               "windows-x86_64",
            ).forEach { os ->
               listOf("dev", "releases").forEach { stage ->
                  artifact("$stage/[revision]/$os/[artifact]-[revision].[ext]")
               }
            }
         }
         content { includeModuleByRegex(".*", ".*kotlin-native-prebuilt.*") }
         metadataSources { artifact() }
      }
      //endregion

      //region Declare the Node.js & Yarn download repositories
      // Workaround https://youtrack.jetbrains.com/issue/KT-68533/
      ivy("https://nodejs.org/dist/") {
         name = "Node Distributions at $url"
         patternLayout { artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]") }
         metadataSources { artifact() }
         content { includeModule("org.nodejs", "node") }
      }
      ivy("https://github.com/yarnpkg/yarn/releases/download") {
         name = "Yarn Distributions at $url"
         patternLayout { artifact("v[revision]/[artifact](-v[revision]).[ext]") }
         metadataSources { artifact() }
         content { includeModule("com.yarnpkg", "yarn") }
      }
      //endregion

      mavenLocal()
   }
}

plugins {
   id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
