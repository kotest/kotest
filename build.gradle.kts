import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
   id("java")
   alias(libs.plugins.kotlin.jvm)
   id("org.jetbrains.intellij.platform") version "2.0.1"
}

repositories {
   mavenCentral()
   mavenLocal()
   maven("https://oss.sonatype.org/content/repositories/snapshots")
   intellijPlatform {
      defaultRepositories()
   }
}


data class PluginDescriptor(
   val since: String, // earliest version string this is compatible with
   val until: String, // latest version string this is compatible with, can be wildcard like 202.*
   // https://github.com/JetBrains/gradle-intellij-plugin#intellij-platform-properties
   val sdkVersion: String, // the version string passed to the intellij sdk gradle plugin
   val sourceFolder: String, // used as the source root for specifics of this build
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

// for the sdk version we can use IC-2021.1 if the product is released
// or IC-213-EAP-SNAPSHOT if not

// for 'since' we can use an early build number without eap/snapshot eg 213.5281.15
// and 'until' we can use a wildcard eg 213.*

val descriptors = listOf(
   PluginDescriptor(
      since = "223.4884.69", // this version is 2022.3
      until = "223.*",
      sdkVersion = "2022.3",
      sourceFolder = "IC-223",
   ),
   PluginDescriptor(
      since = "231.8109.163", // this version is 2023.1 release
      until = "231.*",
      sdkVersion = "2023.1",
      sourceFolder = "IC-231",
   ),
   PluginDescriptor(
      since = "232.5150.116", // this version is 2023.2
      until = "232.*",
      sdkVersion = "2023.2",
      sourceFolder = "IC-232",
   ),
   PluginDescriptor(
      since = "233.9802.16", // this version is 2023.3
      until = "233.*",
      sdkVersion = "2023.3",
      sourceFolder = "IC-233",
   ),
   PluginDescriptor(
      since = "241.15989.150", // this version is 2024.1
      until = "242.*",
      sdkVersion = "2024.1",
      sourceFolder = "IC-241",
   ),
   PluginDescriptor(
      since = "242.*", // this version is 2024.2
      until = "243.*",
      sdkVersion = "2024.2",
      sourceFolder = "IC-242",
   ),
)

val productName = System.getenv("PRODUCT_NAME") ?: "IC-241"
val jvmTargetVersion = System.getenv("JVM_TARGET") ?: "11"
val descriptor = descriptors.first { it.sourceFolder == productName }

val jetbrainsToken: String by project

version = "1.3." + (System.getenv("GITHUB_RUN_NUMBER") ?: "0-SNAPSHOT")

val runWithCustomSandbox by intellijPlatformTesting.runIde.registering {
   prepareSandboxTask {
      sandboxDirectory = project.layout.buildDirectory.dir(project.property("sandbox").toString())
      sandboxSuffix = ""
   }
}

intellijPlatform {
   buildSearchableOptions = false
   projectName = project.name
   instrumentCode = true

   pluginConfiguration {
      name = "kotest-plugin-intellij"
      id = "kotest-plugin-intellij"
      description = "Kotest plugin for IntelliJ IDEA"
      version = project.version.toString()
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
   testImplementation("junit:junit:4.13.2")
   intellijPlatform {
      intellijIdeaCommunity(descriptor.sdkVersion)
      instrumentationTools()
      pluginVerifier()
      zipSigner()
      bundledPlugin("com.intellij.java")
      bundledPlugin("org.jetbrains.kotlin")
      bundledPlugin("org.jetbrains.plugins.gradle")
      testFramework(TestFrameworkType.Platform)
   }

   // we bundle this for 4.1 support
   // in kotest 4.2.0 the launcher has moved to a stand-alone module
   implementation(libs.runtime.kotest.legacy.launcher)

   // this is needed to use the launcher in 4.2.0, in 4.2.1+ the launcher is built
   // into the engine dep which should already be on the classpath
   implementation(libs.runtime.kotest.framework.launcher)

   // needed for the resource files which are loaded into java light tests
   testImplementation(libs.test.kotest.framework.api)
   testImplementation(libs.test.kotest.assertions.core)
   testRuntimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

//   intellijPlatformTesting {
//      runIde
//      testIde
//      testIdeUi
//      testIdePerformance
//   }

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
