object Libs {

   val kotlinVersion = "1.3.61"
   val dokkaVersion = "0.10.0"

   object Arrow {
      private const val version = "0.10.4"
      const val fx = "io.arrow-kt:arrow-fx:0.10.4"
      const val syntax = "io.arrow-kt:arrow-syntax:0.10.4"
      const val validation = "io.arrow-kt:arrow-validation:0.10.4"
   }

   object Allure {
      private const val version = "2.13.1"
      const val commons = "io.qameta.allure:allure-java-commons:2.13.1"
   }

   object JUnitPlatform {
      private const val version = "1.6.0"
      const val engine = "org.junit.platform:junit-platform-engine:1.6.0"
      const val launcher = "org.junit.platform:junit-platform-launcher:1.6.0"
      const val api = "org.junit.platform:junit-platform-suite-api:1.6.0"
      const val testkit = "org.junit.platform:junit-platform-testkit:1.6.0"
   }

   object JUnitJupiter {
      private const val version = "5.6.0"
      const val api = "org.junit.jupiter:junit-jupiter-api:5.6.0"
      const val engine = "org.junit.jupiter:junit-jupiter-engine:5.6.0"
   }

   object Log4j {
      private const val version = "2.13.0"
      const val api = "org.apache.logging.log4j:log4j-api:2.13.0"
      const val core = "org.apache.logging.log4j:log4j-core:2.13.0"
      const val slf4j = "org.apache.logging.log4j:log4j-slf4j-impl:2.13.0"
   }

   object Classgraph {
      const val classgraph = "io.github.classgraph:classgraph:4.8.60"
   }

   object Logback {
      const val classic = "ch.qos.logback:logback-classic:1.2.3"
   }

   object Slf4j {
      const val api = "org.slf4j:slf4j-api:1.7.30"
   }

   object Coroutines {
      private const val version = "1.3.3"
      const val coreCommon = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.3"
      const val jdk8 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.3"
      const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3"
      const val coreJs = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.3"
   }

   object Ktor {
      private const val version = "1.2.6"
      const val serverCore = "io.ktor:ktor-server-core:1.2.6"
      const val serverTestHost = "io.ktor:ktor-server-test-host:1.2.6"
   }
}
