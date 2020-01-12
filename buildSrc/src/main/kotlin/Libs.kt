object Libs {

   val kotlinVersion = "1.3.61"
   val dokkaVersion = "0.10.0"

   object Arrow {
      private const val version = "0.10.4"
      const val fx = "io.arrow-kt:arrow-fx:$version"
      const val syntax = "io.arrow-kt:arrow-syntax:$version"
      const val validation = "io.arrow-kt:arrow-validation:$version"
   }

   object JUnitPlatform {
      private const val version = "1.6.0-RC1"
      const val engine = "org.junit.platform:junit-platform-engine:$version"
      const val launcher = "org.junit.platform:junit-platform-launcher:$version"
      const val api = "org.junit.platform:junit-platform-suite-api:$version"
   }

   object JUnitJupiter {
      private const val version = "5.6.0-RC1"
      const val api = "org.junit.jupiter:junit-jupiter-api:$version"
      const val engine = "org.junit.jupiter:junit-jupiter-engine:$version"
   }

   object Log4j {
      private const val version = "2.13.0"
      const val api = "org.apache.logging.log4j:log4j-api:$version"
      const val core = "org.apache.logging.log4j:log4j-core:$version"
      const val slf4j = "org.apache.logging.log4j:log4j-slf4j-impl:$version"
   }

   object Classgraph {
      const val classgraph = "io.github.classgraph:classgraph:4.8.59"
   }

   object Logback {
      const val classic = "ch.qos.logback:logback-classic:1.2.3"
   }

   object Slf4j {
      const val api = "org.slf4j:slf4j-api:1.7.30"
   }

   object Coroutines {
      private const val version = "1.3.3"
      const val coreCommon = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$version"
      const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
      const val coreJs = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$version"
   }

   object Ktor {
      private const val version = "1.2.6"
      const val serverCore = "io.ktor:ktor-server-core:$version"
      const val serverTestHost = "io.ktor:ktor-server-test-host:$version"
   }
}
