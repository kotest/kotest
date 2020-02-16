object Libs {

   val kotlinVersion = "1.3.61"
   val dokkaVersion = "0.10.0"

   object Arrow {
      private const val version = "0.10.4"
      const val fx = "io.arrow-kt:arrow-fx:$version"
      const val syntax = "io.arrow-kt:arrow-syntax:$version"
      const val validation = "io.arrow-kt:arrow-validation:$version"
   }

   object Allure {
      private const val version = "2.13.1"
      const val commons = "io.qameta.allure:allure-java-commons:$version"
   }

   object JUnitPlatform {
      private const val version = "1.6.0"
      const val engine = "org.junit.platform:junit-platform-engine:$version"
      const val launcher = "org.junit.platform:junit-platform-launcher:$version"
      const val api = "org.junit.platform:junit-platform-suite-api:$version"
      const val testkit = "org.junit.platform:junit-platform-testkit:$version"
   }

   object JUnitJupiter {
      private const val version = "5.6.0"
      const val api = "org.junit.jupiter:junit-jupiter-api:$version"
      const val engine = "org.junit.jupiter:junit-jupiter-engine:$version"
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

   object Mocking {
      const val mockk = "io.mockk:mockk:1.9.3"
   }

   object Coroutines {
      private const val version = "1.3.3"
      const val coreCommon = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$version"
      const val jdk8 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$version"
      const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
      const val coreJs = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$version"
      const val coreNative = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$version"
   }

   object Ktor {
      private const val version = "1.3.0"
      const val serverCore = "io.ktor:ktor-server-core:$version"
      const val serverTestHost = "io.ktor:ktor-server-test-host:$version"
      const val clientJs = "io.ktor:ktor-client-js:$version"
      const val clientCore = "io.ktor:ktor-client-core:$version"
      const val clientApache = "io.ktor:ktor-client-apache:$version"
      const val clientCurl = "io.ktor:ktor-client-curl:$version"
   }
}
