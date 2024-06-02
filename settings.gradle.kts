rootProject.name = "kotest"

pluginManagement {
   repositories {
      mavenCentral()
      gradlePluginPortal()
   }
}

include(
   ":kotest-common",

   // defines data classes and the spec styles; all classes needed to define specs/testcases live here
   ":kotest-framework:kotest-framework-api",

   // async / parallel / concurrency / non-deterministic test helpers
   ":kotest-framework:kotest-framework-concurrency",

   // used to discovery specs from the classpath at runtime
   // brings in the API dependency for required data types
   ":kotest-framework:kotest-framework-discovery",

   // contains the execution engine implementation for jvm, js, native
   // brings in the API dependency
   ":kotest-framework:kotest-framework-engine",

   // a fat jar that includes everything needed to execute the engine as a standalone program
   ":kotest-framework:kotest-framework-standalone",

   // compiler plugins to integrate tests with the engine
   ":kotest-framework:kotest-framework-multiplatform-plugin-embeddable-compiler",
   ":kotest-framework:kotest-framework-multiplatform-plugin-legacy-native",
   ":kotest-framework:kotest-framework-multiplatform-plugin-gradle",

   // contains data driven testing that builds on top of the kotest test framework
   ":kotest-framework:kotest-framework-datatest",

   // contains the matcher interface and is intended as a lightweight dependency for library authors
   // to depend on when writing matcher libraries
   ":kotest-assertions:kotest-assertions-api",

   // contains basic assertion building block such as shouldBe which are used by both
   // framework and assertion libraries;
   // no user should need to depend on this
   ":kotest-assertions:kotest-assertions-shared",

   // the core assertions that cover things like collections, strings, etc
   // users should depend on this if they want to use kotest assertions in tests
   ":kotest-assertions:kotest-assertions-core",
   ":kotest-assertions:kotest-assertions-json",
   ":kotest-assertions:kotest-assertions-sql",

   // base classes for property testing, plus std lib generators
   ":kotest-property",

   // contains  extensions for property testing that build on the kotest test framework
   ":kotest-property:kotest-property-lifecycle",

   // support for executing tests via junit platform through gradle
   // this will also bring in the required libs for the intellij plugin
   ":kotest-runner:kotest-runner-junit5",

   ":kotest-runner:kotest-runner-junit4",
   ":kotest-extensions",
   ":kotest-extensions:kotest-extensions-blockhound",
   ":kotest-extensions:kotest-extensions-http",
   ":kotest-extensions:kotest-extensions-junitxml",
   ":kotest-extensions:kotest-extensions-htmlreporter",

   // allows overriding the .now() functionality on time classes
   ":kotest-extensions:kotest-extensions-now",

   // extensions that adapt junit extensions into kotest extensions
   ":kotest-extensions:kotest-extensions-junit5",

   // the tests modules each test a piece of functionality
   // it is useful to have separate modules so each can set their own project config that
   // may be required as part of the tests
   ":kotest-tests:kotest-tests-autoscan",
   ":kotest-tests:kotest-tests-core",

   ":kotest-tests:kotest-tests-concurrency-tests",
   ":kotest-tests:kotest-tests-concurrency-specs",

   ":kotest-tests:kotest-tests-junitxml",
   ":kotest-tests:kotest-tests-htmlreporter",
   ":kotest-tests:kotest-tests-multipleconfig",
   ":kotest-tests:kotest-tests-test-parallelism",
   ":kotest-tests:kotest-tests-spec-parallelism",
   ":kotest-tests:kotest-tests-tagextension",
   ":kotest-tests:kotest-tests-timeout-project",
   ":kotest-tests:kotest-tests-timeout-sysprop",
   ":kotest-tests:kotest-tests-multiname-test-name-sysprop",
   ":kotest-tests:kotest-tests-native",
//   ":kotest-tests:kotest-tests-js",
   ":kotest-tests:kotest-tests-config-classname",

   // BOM for whole kotest project
   ":kotest-bom",
)

plugins {
   id("com.gradle.develocity") version "3.17.4"
}

develocity {
   buildScan {
      termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
      termsOfUseAgree = "yes"
      publishing.onlyIf { false }
   }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
