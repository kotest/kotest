import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease

plugins {
   id("java")
   alias(libs.plugins.kotlin.jvm)
   id("org.jetbrains.intellij.platform") version "2.2.1"
}

repositories {
   mavenCentral()
   mavenLocal()
   maven("https://oss.sonatype.org/content/repositories/snapshots")

   // IntelliJ Platform Gradle Plugin Repositories Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-repositories-extension.html
   intellijPlatform {
      defaultRepositories()
      jetbrainsRuntime()
   }
}


data class PluginDescriptor(
   val since: String, // earliest version string this is compatible with
   val until: String, // latest version string this is compatible with, can be wildcard like 202.*
   // https://github.com/JetBrains/gradle-intellij-plugin#intellij-platform-properties
   val sdkVersion: String, // the version string passed to the intellij sdk gradle plugin
   val sourceFolder: String, // used as the source root for specifics of this build
   val useInstaller: Boolean, // required to be false for EAP builds
)

// https://jetbrains.org/intellij/sdk/docs/basics/getting_started/build_number_ranges.html
// useful link for kotlin plugin versions:
//    https://plugins.jetbrains.com/plugin/6954-kotlin/versions
// json output of versions:
//    https://jb.gg/intellij-platform-builds-list
// json output but restricted to IDEA ultimate:
//    https://data.services.jetbrains.com/products?fields=code,name,releases.downloads,releases.version,releases.build,releases.type&code=IIU
// when releasing for an EAP, look at snapshots and see the column called build number
//    https://www.jetbrains.com/intellij-repository/snapshots

// for the sdk version we can use IC-241 if the product is released or 243-EAP-SNAPSHOT if not

// for 'since' we can use an early build number without eap/snapshot eg 213.5281.15
// and 'until' we can use a wildcard eg 213.*

// this page shows android studio versions and the corresponding intellij version that is behind it
// https://plugins.jetbrains.com/docs/intellij/android-studio-releases-list.html#2024

val descriptors = listOf(
   PluginDescriptor(
      since = "241.15989.150", // this version is 2024.1.x
      until = "242.*",
      sdkVersion = "2024.1",
      sourceFolder = "IC-241",
      useInstaller = true,
   ),
   PluginDescriptor(
      since = "242.*", // this version is 2024.2.x
      until = "243.*",
      sdkVersion = "2024.2.2",
      sourceFolder = "IC-242",
      useInstaller = true,
   ),
   PluginDescriptor(
      since = "243.*", // this version is 2024.3.x
      until = "244.*",
      sdkVersion = "2024.3.1",
      sourceFolder = "IC-243",
      useInstaller = true,
   ),
   PluginDescriptor(
      since = "251.*", // this version is 2025.1.x
      until = "252.*",
      sdkVersion = "251-EAP-SNAPSHOT",
      sourceFolder = "IC-251",
      useInstaller = false,
   ),
)

val productName = System.getenv("PRODUCT_NAME") ?: "IC-243"
val jvmTargetVersion = System.getenv("JVM_TARGET") ?: "17"
val descriptor = descriptors.first { it.sourceFolder == productName }

val jetbrainsToken: String by project

version = "1.3." + (System.getenv("GITHUB_RUN_NUMBER") ?: "0-SNAPSHOT")

val runWithCustomSandbox by intellijPlatformTesting.runIde.registering {
   prepareSandboxTask {
      sandboxDirectory = project.layout.buildDirectory.dir(project.property("sandbox").toString())
      sandboxSuffix = ""
   }
}

val runWithK2Mode by intellijPlatformTesting.runIde.registering {
   task {
      jvmArgs = listOf("-Didea.kotlin.plugin.use.k2=true")
   }
}

intellijPlatform {
   buildSearchableOptions = false
   projectName = project.name
   instrumentCode = true
   pluginConfiguration {
      name = "kotest"
      id = "kotest-plugin-intellij"
      description = "Kotest individual test support inside the IDE"
      version = project.version.toString() + "-" + descriptor.sdkVersion
      vendor {
         name = "Kotest"
         url = "https://kotest.io"
         email = "sam@sksamuel.com"
      }
   }

   publishing {
      version = project.version.toString() + "-" + descriptor.sdkVersion
      token = System.getenv("JETBRAINS_TOKEN") ?: jetbrainsToken
   }
}

dependencies {
   // https://youtrack.jetbrains.com/issue/IJPL-159134/JUnit5-Test-Framework-refers-to-JUnit4-java.lang.NoClassDefFoundError-junit-framework-TestCase
   testImplementation("junit:junit:4.13.2")

   // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
   intellijPlatform {
      // snapshots here https://www.jetbrains.com/intellij-repository/snapshots/
      intellijIdeaCommunity(descriptor.sdkVersion, useInstaller = descriptor.useInstaller)

      if (!descriptor.useInstaller) {
         jetbrainsRuntime()
      }

      pluginVerifier()
      zipSigner()

      bundledPlugin("com.intellij.java")
      bundledPlugin("org.jetbrains.kotlin")
      bundledPlugin("org.jetbrains.plugins.gradle")

      // this is workaround for a bug in intellij itself
      // see https://jetbrains-platform.slack.com/archives/C5U8BM1MK/p1734228390297349
      if (descriptor.sdkVersion == "2024.3.1") {
         bundledPlugin("com.intellij.llmInstaller")
      }

      testFramework(TestFrameworkType.Plugin.Java)
   }

   // needed for the resource files which are loaded into java light tests
   testImplementation(libs.test.kotest.framework.api)
   testImplementation(libs.test.kotest.assertions.core)
//   testRuntimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

configurations.runtimeOnly {
   exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core-jvm")
   exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
   exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-jdk8")
   exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-test")
   exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-test-jvm")
}

sourceSets {
   main {
      kotlin {
         srcDir("src/${descriptor.sourceFolder}/kotlin")
      }
      resources {
         srcDir("src/${descriptor.sourceFolder}/resources")
      }
   }
}

kotlin {
   compilerOptions {
      jvmToolchain(JavaLanguageVersion.of(jvmTargetVersion).asInt())
   }
}

tasks {
   test {
      isScanForTestClasses = false
      // Only run tests from classes that end with "Test"
      include("**/*Test.class")
      include("**/*Tests.class")
   }
}

tasks {
   printProductsReleases {
      channels = listOf(ProductRelease.Channel.EAP)
      types = listOf(IntelliJPlatformType.IntellijIdeaCommunity)
      untilBuild = provider { null }

      doLast {
         val latestEap = productsReleases.get().max()
         println("Latest EAP build: $latestEap")
      }
   }
}
