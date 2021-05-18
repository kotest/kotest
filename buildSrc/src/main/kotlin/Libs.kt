object Libs {

   const val kotlinVersion = "1.5.0"
   const val dokkaVersion = "0.10.1"
   const val adarshrTestLoggerVersion = "2.0.0"
   const val gradleVersionsPluginVersion = "0.28.0"
   const val gradleEnterprisePluginVersion = "3.5.1"
   const val kotestGradlePlugin = "0.1.4"

   object Kotlin {
      private const val kotlinScriptVersion = "1.5.0"
      const val kotlinScriptRuntime = "org.jetbrains.kotlin:kotlin-script-runtime:$kotlinVersion"
      const val kotlinScriptUtil = "org.jetbrains.kotlin:kotlin-script-util:$kotlinScriptVersion"
      const val kotlinScriptJvm = "org.jetbrains.kotlin:kotlin-scripting-jvm:$kotlinScriptVersion"
      const val compilerEmbeddable = "org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion"
   }

   object Kotest {
      object Extensions {
         const val MockServer = "io.kotest.extensions:kotest-extensions-mockserver:1.0.0"
      }
   }

   object Ajalt {
      const val mordant = "com.github.ajalt:mordant:1.2.1"
   }

   object Jayway {
      const val jsonpath = "com.jayway.jsonpath:json-path:2.5.0"
   }

   object Tschuchortdev {
      private const val version = "1.3.4"
      const val kotlinCompileTesting = "com.github.tschuchortdev:kotlin-compile-testing:$version"
   }

   object JUnit4 {
      private const val version = "4.12"
      const val junit4 = "junit:junit:$version"
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

   object Classgraph {
      const val classgraph = "io.github.classgraph:classgraph:4.8.105"
   }

   object Mocking {
      const val mockk = "io.mockk:mockk:1.9.3"
   }

   object Apache {
      const val commonsio = "commons-io:commons-io:2.6"
      const val commonslang = "org.apache.commons:commons-lang3:3.11"
   }

   object MockServer {
      private const val version = "5.11.2"
      const val netty = "org.mock-server:mockserver-netty:$version"
   }

   object Mifmif {
      const val generex = "com.github.mifmif:generex:1.0.2"
   }

   object OpenTest4j {
      private const val version = "1.2.0"
      const val core = "org.opentest4j:opentest4j:$version"
   }

   object Wumpz {
      const val diffutils = "io.github.java-diff-utils:java-diff-utils:4.9"
   }

   object Coroutines {
      private const val version = "1.5.0"
      const val coreCommon = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
      const val coreJvm = "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$version"
      const val coreJs = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$version"
      const val coreNative = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$version"
      const val jdk8 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$version"
      const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
   }

   object Serialization {
      private const val version = "1.0.1"
      const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:$version"
   }

   object Ktor {
      private const val version = "1.5.3"
      const val clientJs = "io.ktor:ktor-client-js:$version"
      const val clientCore = "io.ktor:ktor-client-core:$version"
      const val clientApache = "io.ktor:ktor-client-apache:$version"
   }

   object KotlinTime {
      private const val version = "0.2.0"
      const val kotlintime = "org.jetbrains.kotlinx:kotlinx-datetime:$version"
   }
}
