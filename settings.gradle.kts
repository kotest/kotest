rootProject.name = "kotest"

pluginManagement {
   repositories {
      google()
      mavenCentral()
      gradlePluginPortal()
   }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
   repositoriesMode = RepositoriesMode.PREFER_SETTINGS

   repositories {
      google()
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
   id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
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

   // generates KMP tests
   ":kotest-framework:kotest-framework-symbol-processor",

   ":kotest-assertions:kotest-assertions-table",

   // provides the base Matcher and assertion counters which are used by the engine to track assertion usage
   ":kotest-assertions:kotest-assertions-shared",

   // the core assertions that cover the basic types such as String, Int, Boolean, etc.
   // it also defines the assertion error builders that create the intellij formatted assertion errors
   // users should depend on this if they want to use kotest assertions in tests
   ":kotest-assertions:kotest-assertions-core",

   // provides json matchers for comparing json strings, json objects, and json arrays
   ":kotest-assertions:kotest-assertions-json",

   // provides matchers for ktor requests and responses
   ":kotest-assertions:kotest-assertions-ktor",

   ":kotest-assertions:kotest-assertions-yaml",

   ":kotest-assertions:kotest-assertions-kotlinx-datetime",

   ":kotest-assertions:kotest-assertions-arrow",
   ":kotest-assertions:kotest-assertions-arrow-fx-coroutines",

   // assertions for the konform validation library
   ":kotest-assertions:kotest-assertions-konform",

   // base classes for property testing, plus std lib generators
   ":kotest-property",

   // contains arbs for kotlinx datetime
   ":kotest-property:kotest-property-datetime",

   // contains  extensions for property testing that build on the kotest test framework
   // the new 6.0+ permutations based DSL for property testing
   ":kotest-property:kotest-property-permutations",

   // contains extensions for property testing that build on the kotest test framework
   ":kotest-property:kotest-property-lifecycle",

   ":kotest-property:kotest-property-arrow",
//   ":kotest-property:kotest-property-arrow-optics",

   // contains some common extensions not worth making a module for
   ":kotest-extensions",

   ":kotest-extensions:kotest-extensions-htmlreporter",
   ":kotest-extensions:kotest-extensions-junitxml",

   // adds support for the koin DI framework - see more https://insert-koin.io/
//   ":kotest-extensions:kotest-extensions-koin",

   // BOM for whole kotest project
   ":kotest-bom",
)

// we can skip modules entirely that are JVM only when running on non-Linux github action CI runners, since
// they have no platform specific code and running on any one platform is sufficient to build and publish.
// this speeds up builds on the slower macos/windows github action runners.
if (System.getenv("CI") != "true" || System.getenv("RUNNER_OS") == "Linux") {
   include(

      // assertions used to validate code does not compile - see more https://github.com/tschuchortdev/kotlin-compile-testing
      ":kotest-assertions:kotest-assertions-compiler",

      // adds support for the allure reporting framework - see more https://allurereport.org/
      ":kotest-extensions:kotest-extensions-allure",
      ":kotest-extensions:kotest-extensions-blockhound",

      // adds support for coroutine decoroutinator - see more https://github.com/Anamorphosee/stacktrace-decoroutinator
      ":kotest-extensions:kotest-extensions-decoroutinator",

      // support for executing tests via junit platform through gradle
      // this will also bring in the required libs for the intellij plugin
      ":kotest-runner:kotest-runner-junit5",

      ":kotest-runner:kotest-runner-junit4",

      // adds support for mockserver - see more https://www.mock-server.com/
      ":kotest-extensions:kotest-extensions-mockserver",

      ":kotest-extensions:kotest-extensions-pitest",

      ":kotest-extensions:kotest-extensions-spring",

      // adds support for the testcontainers framework - see more https://testcontainers.com
      ":kotest-extensions:kotest-extensions-testcontainers",

      // allows overriding the .now() functionality on time classes
      ":kotest-extensions:kotest-extensions-now",

      // extensions that adapt junit extensions into kotest extensions
      ":kotest-extensions:kotest-extensions-junit5",

      // adds support for the wiremock framework - see more https://www.wiremock.io/
      ":kotest-extensions:kotest-extensions-wiremock",

      ":kotest-tests:kotest-tests-core",

      // defines the order of callbacks
      ":kotest-tests:kotest-tests-callback-order",

      ":kotest-tests:kotest-tests-concurrency-tests",
      ":kotest-tests:kotest-tests-concurrency-specs",
      ":kotest-tests:kotest-tests-config-project",
      ":kotest-tests:kotest-tests-config-classname",
      ":kotest-tests:kotest-tests-config-packages",

      // tests that kotest.properties on the classpath are picked up
      ":kotest-tests:kotest-tests-config-properties",

      ":kotest-tests:kotest-tests-htmlreporter",
      ":kotest-tests:kotest-tests-junitxml",
      ":kotest-tests:kotest-tests-junit-displaynameformatter",
      ":kotest-tests:kotest-tests-multiname-test-name-sysprop",
      ":kotest-tests:kotest-tests-power-assert",
      ":kotest-tests:kotest-tests-spec-parallelism",
      ":kotest-tests:kotest-tests-tagextension",
      ":kotest-tests:kotest-tests-timeout-project",
      ":kotest-tests:kotest-tests-timeout-sysprop",
      ":kotest-tests:kotest-tests-test-parallelism",
//   ":kotest-tests:kotest-tests-js",
   )
}

develocity {
   buildScan {
      termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
      termsOfUseAgree = "yes"
      publishing.onlyIf { false }
   }
}

buildCache {
   val kotestUser = providers.gradleProperty("Kotest_GradleBuildCache_user").orNull
   val kotestPass = providers.gradleProperty("Kotest_GradleBuildCache_pass").orNull
//   remote<HttpBuildCache> {
//      url = uri("https://kotest-gradle.duckdns.org/cache")
//      credentials {
//         username = kotestUser
//         password = kotestPass
//      }
//      isPush = kotestUser != null && kotestPass != null
//   }
   local {
      // Disable local cache when running on GitHub Actions to reduce the size of GitHub Actions cache,
      // and to ensure that CI builds updates the remote cache.
      val isCI = System.getenv("CI") == "true"
      isEnabled = !isCI
   }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
