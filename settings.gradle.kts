pluginManagement {
   repositories {
      mavenCentral()
      gradlePluginPortal()
   }
}

val ideaPlatformPrefix: String? = System.getProperty("idea.platform.prefix")
val isIntelliJ = ideaPlatformPrefix.equals("idea", ignoreCase = true)

include("kotest-common")

// defines data classes and the spec styles; all classes needed to define specs/testcases live here
include("kotest-framework:kotest-framework-api")

// async / parallel / concurrency / non-determistic test helpers
include("kotest-framework:kotest-framework-concurrency")

// used to discovery specs from the classpath at runtime
// brings in the API dependency for required data types
include("kotest-framework:kotest-framework-discovery")

// contains the execution engine implementation for jvm, js, native
// brings in the API dependency
include("kotest-framework:kotest-framework-engine")

// a fat jar that includes everything needed to execute the engine as a standalone program
include("kotest-framework:kotest-framework-standalone")

// compiler plugins to integrate tests with the engine
include("kotest-framework:kotest-framework-multiplatform-plugin-embeddable-compiler")
include("kotest-framework:kotest-framework-multiplatform-plugin-legacy-native")
include("kotest-framework:kotest-framework-multiplatform-plugin-gradle")

// contains data driven testing that builds on top of the kotest test framework
include("kotest-framework:kotest-framework-datatest")

// contains the matcher interface and is intended as a lightweight dependency for library authors
// to depend on when writing matcher libraries
include("kotest-assertions:kotest-assertions-api")

// contains basic assertion building block such as shouldBe which are used by both
// framework and assertion libraries;
// no user should need to depend on this
include("kotest-assertions:kotest-assertions-shared")

// the core assertions that cover things like collections, strings, etc
// users should depend on this if they want to use kotest assertions in tests
include("kotest-assertions:kotest-assertions-core")
include("kotest-assertions:kotest-assertions-json")
include("kotest-assertions:kotest-assertions-sql")

// base classes for property testing, plus std lib generators
include("kotest-property")

// contains  extensions for property testing that build on the kotest test framework
include("kotest-property:kotest-property-lifecycle")

// support for executing tests via junit platform through gradle
// this will also bring in the required libs for the intellij plugin
include("kotest-runner:kotest-runner-junit5")

include("kotest-runner:kotest-runner-junit4")
include("kotest-extensions")
include("kotest-extensions:kotest-extensions-http")
include("kotest-extensions:kotest-extensions-junitxml")
include("kotest-extensions:kotest-extensions-htmlreporter")

// extensions that adapt junit extensions into kotest extensions
include("kotest-extensions:kotest-extensions-junit5")

// the tests modules each test a piece of functionality
// it is useful to have separate modules so each can set their own project config that
// may be required as part of the tests
include("kotest-tests:kotest-tests-autoscan")
include("kotest-tests:kotest-tests-core")

include("kotest-tests:kotest-tests-concurrency-tests")
include("kotest-tests:kotest-tests-concurrency-specs")

include("kotest-tests:kotest-tests-junitxml")
include("kotest-tests:kotest-tests-htmlreporter")
include("kotest-tests:kotest-tests-multipleconfig")
include("kotest-tests:kotest-tests-test-parallelism")
include("kotest-tests:kotest-tests-spec-parallelism")
include("kotest-tests:kotest-tests-tagextension")
include("kotest-tests:kotest-tests-timeout-project")
include("kotest-tests:kotest-tests-timeout-sysprop")
include("kotest-tests:kotest-tests-multiname-test-name-sysprop")
include("kotest-tests:kotest-tests-native")
//include("kotest-tests:kotest-tests-js")
include("kotest-tests:kotest-tests-project-configuration")
include("kotest-tests:kotest-tests-config-classname")

// BOM for whole kotest project
include("kotest-bom")

plugins {
   id("com.gradle.enterprise") version "3.11.1"
}

gradleEnterprise {
   buildScan {
      termsOfServiceUrl = "https://gradle.com/terms-of-service"
      termsOfServiceAgree = "yes"
   }
}
