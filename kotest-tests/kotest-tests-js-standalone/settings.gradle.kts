rootProject.name = "kotest-tests-js-standalone"

pluginManagement {
   includeBuild("../..")

   repositories {
      mavenCentral {
         mavenContent { excludeGroup("io.kotest") }
      }
      gradlePluginPortal {
         content { excludeGroup("io.kotest") }
      }
   }
}

includeBuild("../..")
