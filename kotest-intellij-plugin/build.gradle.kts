import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease

data class PluginDescriptor(
   val since: String, // earliest version string this is compatible with
   val until: String, // latest version string this is compatible with, can be wildcard like 202.*
   // https://github.com/JetBrains/gradle-intellij-plugin#intellij-platform-properties
   val sdkVersion: String, // the version string passed to the intellij sdk Gradle plugin, take released versions from https://www.jetbrains.com/idea/download/other.html
   val sourceFolder: String, // used as the source root for specifics of this build
   val useInstaller: Boolean, // required to be false for EAP builds
   val jdkTarget: JavaVersion,
   val androidVersion: String, // android plugin version
   val webpPlugin: String?, // for newer intellij, this is no longer bundled and must be specified
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
      since = "251.*", // this version is 2025.1.x
      until = "261.*",
      sdkVersion = "2025.1.7",
      sourceFolder = "IC-251",
      useInstaller = true,
      jdkTarget = JavaVersion.VERSION_21,
      androidVersion = "251.23774.200",
      webpPlugin = null,
   ),
   PluginDescriptor(
      since = "252.*", // this version is 2025.2.x
      until = "253.*",
      sdkVersion = "2025.2.6.1",
      sourceFolder = "IC-252",
      useInstaller = true,
      jdkTarget = JavaVersion.VERSION_21,
      androidVersion = "252.23892.458",
      webpPlugin = null,
   ),
   PluginDescriptor(
      since = "253.*", // this version is 2025.3.x
      until = "261.*",
      sdkVersion = "2025.3.2",
      sourceFolder = "IC-253",
      useInstaller = true,
      jdkTarget = JavaVersion.VERSION_21,
      androidVersion = "253.28294.334",
      webpPlugin = "intellij.webp:253.28294.218",
   ),
   PluginDescriptor(
      since = "261.*", // this version is 2026.1.x
      until = "262.*",
      sdkVersion = "261-EAP-SNAPSHOT",
      sourceFolder = "IC-261",
      useInstaller = false,
      jdkTarget = JavaVersion.VERSION_21,
      androidVersion = "261.20869.38",
      webpPlugin = "intellij.webp:261.21525.28",
   ),
)

val productName = System.getenv("PRODUCT_NAME") ?: "IC-253"
val descriptor: PluginDescriptor = descriptors.first { it.sourceFolder == productName }
val jvmTargetVersion: String = System.getenv("JVM_TARGET") ?: descriptor.jdkTarget.majorVersion

plugins {
   id("org.jetbrains.intellij.platform") version "2.11.0"
   kotlin("jvm")
}

repositories {
   mavenCentral()
   mavenLocal()
   maven("https://central.sonatype.com/repository/maven-snapshots")

   // IntelliJ Platform Gradle Plugin Repositories Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-repositories-extension.html
   intellijPlatform {
      jetbrainsRuntime()
      defaultRepositories() // Includes the necessary JetBrains repositories
      marketplace()         // Specifically enables Marketplace plugin resolution
   }
}


val jetbrainsToken: String by project

// note GITHUB_RUN_NUMBER is reset whenever the workflow name changes
version = "6.1." + (System.getenv("GITHUB_RUN_NUMBER") ?: "0-SNAPSHOT")

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
   // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
   intellijPlatform {
      // snapshots here https://www.jetbrains.com/intellij-repository/snapshots/
      // released versions list https://www.jetbrains.com/idea/download/other.html
      intellijIdea(descriptor.sdkVersion) {
         useInstaller = descriptor.useInstaller
      }

      if (!descriptor.useInstaller) {
         jetbrainsRuntime()
      }

      pluginVerifier()
      zipSigner()

      bundledPlugin("com.intellij.java")
      bundledPlugin("JUnit")
      bundledPlugin("com.intellij.gradle")
      bundledPlugin("com.intellij.modules.json")
      bundledPlugin("com.intellij.properties")
      bundledPlugin("com.intellij.platform.images")
      bundledPlugin("org.intellij.groovy")
      bundledPlugin("org.intellij.intelliLang")
      bundledPlugin("org.jetbrains.idea.gradle.dsl")
      bundledPlugin("org.jetbrains.kotlin")
      bundledPlugin("org.jetbrains.plugins.gradle")
      bundledPlugin("org.toml.lang")
      if (descriptor.webpPlugin == null)
         bundledPlugin("intellij.webp")
      else
         plugin(descriptor.webpPlugin)
      plugin("org.jetbrains.android:${descriptor.androidVersion}")

      testFramework(TestFrameworkType.Platform)
      testFramework(TestFrameworkType.Plugin.Java)
   }

   implementation("org.jetbrains:annotations:26.0.2-1")

   // https://youtrack.jetbrains.com/issue/IJPL-159134/JUnit5-Test-Framework-refers-to-JUnit4-java.lang.NoClassDefFoundError-junit-framework-TestCase
   testImplementation(libs.junit4)

   // needed for the resource files which are loaded into java light tests
   testImplementation(libs.test.kotest.framework.api)
   testImplementation(libs.test.kotest.assertions.core)
//   testRuntimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

intellijPlatformTesting {
   runIde {
      register("runWithOptionalPlugins") {
         plugins {
            plugin("org.jetbrains.android:${descriptor.androidVersion}")
            plugin("intellij.webp:253.28294.218")
         }
      }
   }
}

configurations.runtimeOnly {
   exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core-jvm")
   exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
   exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-jdk8")
   exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-test")
   exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-test-jvm")
}

// allows us to have different implementations of the same logic for different intellij versions
// useful when we want to move from a deprecated function to a non-deprecated one in a more recent intellij version
sourceSets {
   main {
      kotlin {
         srcDir("src/${descriptor.sourceFolder}/kotlin")
      }
      resources {
         srcDir("src/${descriptor.sourceFolder}/resources")
      }
   }
   test {
      dependencies {
         implementation(kotlin("stdlib"))
      }
   }
}

kotlin {
   jvmToolchain { languageVersion = JavaLanguageVersion.of(jvmTargetVersion) }
   compilerOptions {
      jvmToolchain(JavaLanguageVersion.of(jvmTargetVersion).asInt())
      optIn.set(listOf("org.jetbrains.kotlin.analysis.api.permissions.KaAllowProhibitedAnalyzeFromWriteAction"))
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
   // Configures a task to print the latest EAP build
   printProductsReleases {
      channels = listOf(ProductRelease.Channel.EAP)
      types = listOf(IntelliJPlatformType.IntellijIdea)
      untilBuild = provider { null }

      doLast {
         val latestEap = productsReleases.get().max()
         println("Latest EAP build: $latestEap")
      }
   }
}
