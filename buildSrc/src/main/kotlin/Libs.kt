object Libs {

   object JUnitPlatform {
      private const val version = "1.6.0-M1"
      const val engine = "org.junit.platform:junit-platform-engine:$version"
      const val launcher = "org.junit.platform:junit-platform-launcher:$version"
      const val api = "org.junit.platform:junit-platform-suite-api:$version"
   }

   object JUnitJupiter {
      const val api = "org.junit.jupiter:junit-jupiter-api:5.6.0-M1"
   }

   object Slf4j {
      const val api = "org.slf4j:slf4j-api:1.7.25"
   }

   object Coroutines {
      const val version = "1.3.3"
      val coreCommon = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$version"
      val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
      val coreJs = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$version"
   }

   object Ktor {
      const val version = "1.2.6"
      val serverCore = "io.ktor:ktor-server-core:$version"
      val serverTestHost = "io.ktor:ktor-server-test-host:$version"
   }
}

