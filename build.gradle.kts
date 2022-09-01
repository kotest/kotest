import Ci.publishVersion

plugins {
   `version-catalog`
   `maven-publish`
   `kotest-publishing-conventions`
}

repositories {
   mavenCentral()
   mavenLocal()
   maven("https://oss.sonatype.org/content/repositories/snapshots/") {
      mavenContent { snapshotsOnly() }
   }
   google()
   gradlePluginPortal() // tvOS builds need to be able to fetch a kotlin gradle plugin
}

catalog {
   val groupId = project.group.toString()
   val extensionsGroupId = "$groupId.extensions"

   println("using groupId: $groupId and version: $publishVersion for version catalog")

   versionCatalog {
      listOf(
         "assertions-api",
         "assertions-core",
         "assertions-json",
         "assertions-shared",
         "assertions-sql",
         "framework-api",
         "framework-concurrency",
         "framework-datatest",
         "framework-discovery",
         "framework-engine",
         "property",
         "runner-junit4",
         "runner-junit5",
         "ext-htmlreporter", // Extension built and released with main kotest project
         "ext-junitxml" // Extension built and released with main kotest project
      ).forEach { alias ->
         library(alias, groupId, "kotest-$alias").version(publishVersion)
      }
   }
}

publishing {
   publications {
      create<MavenPublication>("KotestVersionCatalog") {
         from(components["versionCatalog"])
         groupId = "io.kotest"
         artifactId = "kotest-version-catalog"
      }
   }
}
