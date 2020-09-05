object Libs {

   const val kotlinVersion = "1.4.0"
   const val dokkaVersion = "0.10.1"
   const val adarshrTestLoggerVersion = "2.0.0"
   const val gradleVersionsPluginVersion = "0.28.0"
   const val kotestGradlePlugin = "0.1.3"

   object Arrow {
      private const val version = "0.10.5"
      const val core = "io.arrow-kt:arrow-core:$version"
      const val fx = "io.arrow-kt:arrow-fx:$version"
      const val syntax = "io.arrow-kt:arrow-syntax:$version"
      const val validation = "io.arrow-kt:arrow-validation:$version"
   }

   object Ajalt {
      const val clikt = "com.github.ajalt:clikt:2.8.0"
      const val mordant = "com.github.ajalt:mordant:1.2.1"
   }

   object Allure {
      private const val version = "2.13.5"
      const val commons = "io.qameta.allure:allure-java-commons:$version"
   }

   object JSoup {
      const val jsoup = "org.jsoup:jsoup:1.13.1"
   }

   object Jayway {
      const val jsonpath = "com.jayway.jsonpath:json-path:2.4.0"
   }

   object Jackson {
      private const val version = "2.10.2"
      const val kotlin = "com.fasterxml.jackson.module:jackson-module-kotlin:$version"
   }

   object Koin {
      private const val version = "2.1.6"
      const val core = "org.koin:koin-core:$version"
      const val test = "org.koin:koin-test:$version"
   }

   object Tschuchortdev {
      private const val version = "1.2.9"
      const val kotlinCompileTesting = "com.github.tschuchortdev:kotlin-compile-testing:$version"
   }

   object JUnitPlatform {
      private const val version = "1.6.2"
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
      private const val version = "5.6.2"
      const val api = "org.junit.jupiter:junit-jupiter-api:$version"
      const val engine = "org.junit.jupiter:junit-jupiter-engine:$version"
   }

   object Konform {
      const val Konform = "io.konform:konform:0.2.0"
      const val KonformJs = "io.konform:konform-js:0.2.0"
      const val KonformJvm = "io.konform:konform-jvm:0.2.0"
   }

   object Classgraph {
      const val classgraph = "io.github.classgraph:classgraph:4.8.88"
   }

   object Mocking {
      const val mockk = "io.mockk:mockk:1.9.3"
   }

   object Apache {
      const val commonsio = "commons-io:commons-io:2.6"
   }

   object MockServer {
      private const val version = "5.11.1"
      const val netty = "org.mock-server:mockserver-netty:$version"
      const val javaClient = "org.mock-server:mockserver-client-java:$version"
   }

   object Mifmif {
      const val generex = "com.github.mifmif:generex:1.0.2"
   }

   object OpenTest4j {
      private const val version = "1.2.0"
      const val core = "org.opentest4j:opentest4j:$version"
   }

   object Wumpz {
      const val diffutils = "io.github.java-diff-utils:java-diff-utils:4.5"
   }

   object TestContainers {
      private const val version = "1.14.3"
      const val testcontainers = "org.testcontainers:testcontainers:$version"
   }

   object Spring {
      private const val version = "5.2.8.RELEASE"
      const val context = "org.springframework:spring-context:$version"
      const val test = "org.springframework:spring-test:$version"
   }

   object Pitest {
      const val pitest = "org.pitest:pitest:1.4.11"
   }

   object Bytebuddy {
      const val bytebuddy = "net.bytebuddy:byte-buddy:1.10.14"
   }

   object Coroutines {
      private const val version = "1.3.9"
      const val coreCommon = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
      const val coreJvm = "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$version"
      const val coreJs = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$version"
      const val coreNative = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$version"
      const val coreLinux = "org.jetbrains.kotlinx:kotlinx-coroutines-core-linuxx64:$version"
      const val coreMingw = "org.jetbrains.kotlinx:kotlinx-coroutines-core-mingwx64:$version"
      const val coreMacos = "org.jetbrains.kotlinx:kotlinx-coroutines-core-macosx64:$version"
      const val jdk8 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$version"
   }

   object Ktor {
      private const val version = "1.4.0"
      const val serverCore = "io.ktor:ktor-server-core:$version"
      const val serverTestHost = "io.ktor:ktor-server-test-host:$version"
      const val clientJs = "io.ktor:ktor-client-js:$version"
      const val clientCore = "io.ktor:ktor-client-core:$version"
      const val clientCoreJvm = "io.ktor:ktor-client-core-jvm:$version"
      const val clientCioJvm = "io.ktor:ktor-client-cio-jvm:$version"
      const val clientApache = "io.ktor:ktor-client-apache:$version"
   }

   object Klock {
      private const val version = "1.12.0"
      const val klock = "com.soywiz.korlibs.klock:klock:$version"
   }

   object KotlinTime {
      private const val version = "0.1.0"
      const val kotlintime = "org.jetbrains.kotlinx:kotlinx-datetime:$version"
   }
}
