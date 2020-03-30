object Libs {

   const val kotlinVersion = "1.3.71"
   const val dokkaVersion = "0.10.1"
   const val adarshrTestLoggerVersion = "2.0.0"
   const val gradleVersionsPluginVersion = "0.28.0"

   object Arrow {
      private const val version = "0.10.4"
      const val fx = "io.arrow-kt:arrow-fx:$version"
      const val syntax = "io.arrow-kt:arrow-syntax:$version"
      const val validation = "io.arrow-kt:arrow-validation:$version"
   }

   object Allure {
      private const val version = "2.13.2"
      const val commons = "io.qameta.allure:allure-java-commons:$version"
   }

   object Ajalt {
      val clikt = "com.github.ajalt:clikt:2.6.0"
      val mordant = "com.github.ajalt:mordant:1.2.1"
   }

   object JUnitPlatform {
      private const val version = "1.6.0"
      const val commons = "org.junit.platform:junit-platform-commons:$version"
      const val engine = "org.junit.platform:junit-platform-engine:$version"
      const val launcher = "org.junit.platform:junit-platform-launcher:$version"
      const val api = "org.junit.platform:junit-platform-suite-api:$version"
      const val testkit = "org.junit.platform:junit-platform-testkit:$version"
   }

   object Jdom {
      const val jdom2 = "org.jdom:jdom2:2.0.6"
   }

   object JUnitJupiter {
      private const val version = "5.6.0"
      const val api = "org.junit.jupiter:junit-jupiter-api:$version"
      const val engine = "org.junit.jupiter:junit-jupiter-engine:$version"
   }

   object Konform {
      const val Konform = "io.konform:konform:0.2.0"
      const val KonformJs = "io.konform:konform-js:0.2.0"
      const val KonformJvm = "io.konform:konform-jvm:0.2.0"
   }

   object Classgraph {
      const val classgraph = "io.github.classgraph:classgraph:4.8.65"
   }

   object Mocking {
      const val mockk = "io.mockk:mockk:1.9.3"
   }

   object Mifmif {
      const val generex = "com.github.mifmif:generex:1.0.2"
   }

   object Wumpz {
      const val diffutils = "com.github.wumpz:diffutils:2.2"
   }

   object Coroutines {
      private const val version = "1.3.5"
      const val coreCommon = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$version"
      const val jdk8 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$version"
      const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
      const val coreJs = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$version"
      const val coreNative = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$version"
   }

   object Ktor {
      private const val version = "1.3.1"
      const val serverCore = "io.ktor:ktor-server-core:$version"
      const val serverTestHost = "io.ktor:ktor-server-test-host:$version"
      const val clientJs = "io.ktor:ktor-client-js:$version"
      const val clientCore = "io.ktor:ktor-client-core:$version"
      const val clientApache = "io.ktor:ktor-client-apache:$version"
      const val clientCurl = "io.ktor:ktor-client-curl:$version"
   }

   object Klock {
      private const val version = "1.7.5"
      const val klock = "com.soywiz.korlibs.klock:klock:$version"
   }
}
