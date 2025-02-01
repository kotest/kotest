rootProject.name = "kotest"

pluginManagement {
   repositories {
      mavenCentral()
      gradlePluginPortal()
   }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
   repositoriesMode = RepositoriesMode.PREFER_SETTINGS

   repositories {
      mavenCentral()
      maven("https://oss.sonatype.org/content/repositories/snapshots/") {
         name = "SonatypeSnapshots"
         mavenContent { snapshotsOnly() }
      }
      maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
         name = "SonatypeSnapshots2"
         mavenContent { snapshotsOnly() }
      }

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
   id("com.gradle.develocity") version "3.17.5"
   id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include(
   ":kotest-common",

   // contains the execution engine implementation for jvm, js, native
   // brings in the API dependency
   ":kotest-framework:kotest-framework-engine",

   // a fat jar that includes everything needed to execute the engine as a standalone program
   ":kotest-framework:kotest-framework-standalone",

   // gradle plugin to run tests outside of gradle's test task
   ":kotest-framework:kotest-framework-plugin-gradle",

   // compiler plugins to integrate tests with the engine
   ":kotest-framework:kotest-framework-multiplatform-plugin-embeddable-compiler",
   ":kotest-framework:kotest-framework-multiplatform-plugin-legacy-native",
   ":kotest-framework:kotest-framework-multiplatform-plugin-gradle",

   // contains basic assertion building block such as shouldBe which are used by both
   // framework and assertion libraries;
   // no user should need to depend on this
   ":kotest-assertions:kotest-assertions-shared",

   // the core assertions that cover things like collections, strings, etc
   // users should depend on this if they want to use kotest assertions in tests
   ":kotest-assertions:kotest-assertions-core",
   ":kotest-assertions:kotest-assertions-json",
   ":kotest-assertions:kotest-assertions-ktor",
   ":kotest-assertions:kotest-assertions-yaml",
   ":kotest-assertions:kotest-assertions-compiler",
   ":kotest-assertions:kotest-assertions-kotlinx-datetime",

   // base classes for property testing, plus std lib generators
   ":kotest-property",

   // the new 6.0+ permutations based DSL for property testing
   ":kotest-property:kotest-property-permutations",

   // contains extensions for property testing that build on the kotest test framework
   ":kotest-property:kotest-property-lifecycle",

   // support for executing tests via junit platform through gradle
   // this will also bring in the required libs for the intellij plugin
   ":kotest-runner:kotest-runner-junit5",

   ":kotest-runner:kotest-runner-junit4",

   // contains some common extensions not worth making a module for
   ":kotest-extensions",

   // adds support for the allure reporting framework - see more https://allurereport.org/
   ":kotest-extensions:kotest-extensions-allure",
   ":kotest-extensions:kotest-extensions-blockhound",
   ":kotest-extensions:kotest-extensions-htmlreporter",
   ":kotest-extensions:kotest-extensions-junitxml",

   // adds support for the koin DI framework - see more https://insert-koin.io/
   ":kotest-extensions:kotest-extensions-koin",
   ":kotest-extensions:kotest-extensions-spring",

   // allows overriding the .now() functionality on time classes
   ":kotest-extensions:kotest-extensions-now",

   // extensions that adapt junit extensions into kotest extensions
   ":kotest-extensions:kotest-extensions-junit5",

   // the tests modules each test a piece of functionality
   // it is useful to have separate modules so each can set their own project config that
   // may be required as part of the tests
   ":kotest-tests:kotest-tests-core",

   ":kotest-tests:kotest-tests-concurrency-tests",
   ":kotest-tests:kotest-tests-concurrency-specs",
   ":kotest-tests:kotest-tests-config-classname",
   ":kotest-tests:kotest-tests-config-packages",
   ":kotest-tests:kotest-tests-htmlreporter",
   ":kotest-tests:kotest-tests-junitxml",
   ":kotest-tests:kotest-tests-multiname-test-name-sysprop",
   ":kotest-tests:kotest-tests-power-assert",
   ":kotest-tests:kotest-tests-spec-parallelism",
   ":kotest-tests:kotest-tests-tagextension",
   ":kotest-tests:kotest-tests-timeout-project",
   ":kotest-tests:kotest-tests-timeout-sysprop",
   ":kotest-tests:kotest-tests-test-parallelism",
//   ":kotest-tests:kotest-tests-js",

   // BOM for whole kotest project
   ":kotest-bom",
)

develocity {
   buildScan {
      termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
      termsOfUseAgree = "yes"
      publishing.onlyIf { false }
   }
}

//buildCache {
//   val kotestUser = providers.gradleProperty("Kotest_GradleBuildCache_user").orNull
//   val kotestPass = providers.gradleProperty("Kotest_GradleBuildCache_pass").orNull
//   remote<HttpBuildCache> {
//      url = uri("https://kotest-gradle.duckdns.org/cache")
//      credentials {
//         username = kotestUser
//         password = kotestPass
//      }
//      isPush = kotestUser != null && kotestPass != null
//   }
//   local {
//      // Disable local cache when running on GitHub Actions to reduce the size of GitHub Actions cache,
//      // and to ensure that CI builds updates the remote cache.
//      val isCI = System.getenv("CI") == "true"
//      isEnabled = !isCI
//   }
//}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
