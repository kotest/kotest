import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

buildscript {
   repositories {
      mavenCentral()
   }
}

plugins {
   java
   kotlin("jvm").version("1.6.21")
   id("org.jetbrains.intellij") version "1.6.0"
}

repositories {
   mavenCentral()
   mavenLocal()
   maven("https://oss.sonatype.org/content/repositories/snapshots")
   maven("https://www.jetbrains.com/intellij-repository/snapshots")
}


data class PluginDescriptor(
   val since: String, // earliest version string this is compatible with
   val until: String, // latest version string this is compatible with, can be wildcard like 202.*
   // https://github.com/JetBrains/gradle-intellij-plugin#intellij-platform-properties
   val sdkVersion: String, // the version string passed to the intellij sdk gradle plugin
   val sourceFolder: String, // used as the source root for specifics of this build
   val deps: List<String> // dependent plugins of this plugin
)

// https://jetbrains.org/intellij/sdk/docs/basics/getting_started/build_number_ranges.html
// useful link for kotlin plugin versions:
//    https://plugins.jetbrains.com/plugin/6954-kotlin/versions
// json output of versions:
//    https://jb.gg/intellij-platform-builds-list
// json output but restricted to IDEA ultimate:
//    https://data.services.jetbrains.com/products?fields=code,name,releases.downloads,releases.version,releases.build,releases.type&code=IIU
// when releasing for an EAP, look at snapshots
//    https://www.jetbrains.com/intellij-repository/snapshots

// for the sdk version we can use IC-2021.1 if the product is released
// or IC-213-EAP-SNAPSHOT if not

// for 'since' we can use an early build number without eap/snapshot eg 213.5281.15
// and 'until' we can use a wildcard eg 213.*

val plugins = listOf(
   PluginDescriptor(
      since = "211.6693.111", // this version is 2021.1
      until = "211.*",
      sdkVersion = "IC-2021.1",
      sourceFolder = "IC-211",
      deps = listOf("java", "org.jetbrains.plugins.gradle", "org.jetbrains.kotlin")
   ),
   PluginDescriptor(
      since = "212.3116.43", // this version is 2021.2
      until = "212.*",
      sdkVersion = "IC-2021.2.3",
      sourceFolder = "IC-212",
      deps = listOf("java", "org.jetbrains.plugins.gradle", "org.jetbrains.kotlin")
   ),
   PluginDescriptor(
      since = "213.3714", // this version is 2021.3
      until = "213.*",
      sdkVersion = "IC-2021.3",
      sourceFolder = "IC-213",
      deps = listOf("java", "org.jetbrains.plugins.gradle", "org.jetbrains.kotlin:213-1.6.10-release-923-IJ5744.223")
   ),
   PluginDescriptor(
      since = "221.3427.89", // this version is 2022.1
      until = "221.*",
      sdkVersion = "IC-2022.1",
      sourceFolder = "IC-221",
      deps = listOf("java", "org.jetbrains.plugins.gradle", "org.jetbrains.kotlin")
   ),
   PluginDescriptor(
      since = "222.2270.16", // this version is 2022.2 EAP 2
      until = "222.*",
      sdkVersion = "IC-222-EAP-SNAPSHOT",
      sourceFolder = "IC-222",
      deps = listOf("java", "org.jetbrains.plugins.gradle", "org.jetbrains.kotlin")
   )
)

val productName = System.getenv("PRODUCT_NAME") ?: "IC-203"
val descriptor = plugins.first { it.sourceFolder == productName }

val jetbrainsToken: String by project

version = "1.2." + (System.getenv("GITHUB_RUN_NUMBER") ?: "0-SNAPSHOT")

intellij {
   sandboxDir.set(project.property("sandbox").toString())
//   sandboxDir.set("./sandbox")
   version.set(descriptor.sdkVersion)
   pluginName.set("kotest-plugin-intellij")
   plugins.addAll(*descriptor.deps.toTypedArray())
   downloadSources.set(true)
   type.set("IC")
   updateSinceUntilBuild.set(false)
}

dependencies {
   implementation("javax.xml.bind:jaxb-api:2.3.1")
   implementation("javax.activation:activation:1.1.1")

   // we bundle this for 4.1 support
   // in kotest 4.2.0 the launcher has moved to a stand alone module
   implementation("io.kotest:kotest-launcher:1.0.10")

   // this is needed to use the launcher in 4.2.0, in 4.2.1+ the launcher is built
   // into the engine dep which should already be on the classpath
   implementation("io.kotest:kotest-framework-launcher-jvm:4.2.0")

   // needed for the resource files which are loaded into java light tests
   testImplementation("io.kotest:kotest-framework-api:5.3.1")
   testImplementation("io.kotest:kotest-assertions-core-jvm:5.3.1")
}

sourceSets {
   main {
      withConvention(KotlinSourceSet::class) {
         kotlin.srcDirs("src/${descriptor.sourceFolder}/kotlin")
      }
      resources {
         srcDir("src/${descriptor.sourceFolder}/resources")
      }
   }
}

tasks {

   compileKotlin {
      kotlinOptions {
         jvmTarget = "11"
      }
   }

   withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
      kotlinOptions {
         jvmTarget = "11"
      }
   }

   buildPlugin {
      archiveClassifier.set(descriptor.sdkVersion)
   }

   publishPlugin {
      token.set(System.getenv("JETBRAINS_TOKEN") ?: jetbrainsToken)
   }

   patchPluginXml {
      version.set("${project.version}-${descriptor.sdkVersion}")
      sinceBuild.set(descriptor.since)
      untilBuild.set(descriptor.until)
   }

   test {
      isScanForTestClasses = false
      // Only run tests from classes that end with "Test"
      include("**/*Test.class")
      include("**/*Tests.class")
   }
}
