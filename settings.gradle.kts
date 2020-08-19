enableFeaturePreview("GRADLE_METADATA")

pluginManagement {
   repositories {
      mavenCentral()
      gradlePluginPortal()
      maven("https://dl.bintray.com/kotlin/kotlin-eap")
      jcenter()
   }
}

include("kotest-common")

// defines data classes and the spec styles; all classes needed to define specs/testcases live here
include("kotest-framework:kotest-framework-api")

// used to discovery specs from the classpath at runtime
// brings in the API dependency for required data types
include("kotest-framework:kotest-framework-discovery")

// public api contract that tools can use to launch instances of the kotest engine
// tools can depend on this module and it won't bring in other dependencies, instead requires
// that user add the engine dependency at runtime
include("kotest-framework:kotest-framework-launcher")

// contains the JVM execution engine implementation
// brings in the API dependency
include("kotest-framework:kotest-framework-engine")

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
include("kotest-assertions:kotest-assertions-arrow")
include("kotest-assertions:kotest-assertions-json")
include("kotest-assertions:kotest-assertions-ktor")
include("kotest-assertions:kotest-assertions-jsoup")
include("kotest-assertions:kotest-assertions-konform")
include("kotest-assertions:kotest-assertions-kotlinx-time")
include("kotest-assertions:kotest-assertions-compiler")
include("kotest-assertions:kotest-assertions-klock")
include("kotest-assertions:kotest-assertions-sql")
include("kotest-property")
include("kotest-property:kotest-property-arrow")
include("kotest-runner:kotest-runner-junit5")
include("kotest-runner:kotest-runner-junit4")
include("kotest-extensions")
include("kotest-extensions:kotest-extensions-allure")
include("kotest-extensions:kotest-extensions-http")
include("kotest-extensions:kotest-extensions-junitxml")
include("kotest-extensions:kotest-extensions-koin")
include("kotest-extensions:kotest-extensions-mockserver")
include("kotest-extensions:kotest-extensions-spring")
include("kotest-extensions:kotest-extensions-testcontainers")
include("kotest-extensions:kotest-extensions-junit5extensions")
include("kotest-plugins:kotest-plugins-pitest")
include("kotest-tests:kotest-tests-autoscan")
include("kotest-tests:kotest-tests-core")
include("kotest-tests:kotest-tests-junitxml")
include("kotest-tests:kotest-tests-multipleconfig")
include("kotest-tests:kotest-tests-parallelism")
include("kotest-tests:kotest-tests-projectlistener")
include("kotest-tests:kotest-tests-timeout")
include("kotest-tests:kotest-tests-timeout-sysprop")
include("kotest-examples:kotest-examples-javascript")
include("kotest-examples:kotest-examples-jvm")
include("kotest-examples:kotest-examples-allure")
include("kotest-examples:kotest-examples-spring-webflux")
