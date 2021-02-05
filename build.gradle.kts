import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

buildscript {
   repositories {
      mavenCentral()
   }
}

plugins {
   kotlin("jvm")
   java
   id("org.jetbrains.intellij").version("0.6.5")
}

repositories {
   mavenCentral()
   mavenLocal()
   maven("https://oss.sonatype.org/content/repositories/snapshots")
   maven("https://www.jetbrains.com/intellij-repository/snapshots")
}


// useful link for plugin versions https://plugins.jetbrains.com/plugin/6954-kotlin/versions
// https://jetbrains.org/intellij/sdk/docs/basics/getting_started/build_number_ranges.html
// json output of versions: https://jb.gg/intellij-platform-builds-list
// json for ultimate https://data.services.jetbrains.com/products?fields=code,name,releases.downloads,releases.version,releases.build,releases.type&code=IIU
// when releasing for an EAP, look at snapshots: https://www.jetbrains.com/intellij-repository/snapshots and use eg 211-EAP-SNAPSHOT
val plugins = listOf(
   plugin.PluginDescriptor(
      since = "193.4099.13",
      until = "193.*",
      sdkVersion = "IC-2019.3",
      sourceFolder = "IC-193",
      deps = listOf("java", "org.jetbrains.plugins.gradle", "org.jetbrains.kotlin:1.3.72-release-IJ2019.3-5")
   ),
   plugin.PluginDescriptor(
      since = "201.6487",
      until = "201.*",
      sdkVersion = "IC-2020.1",
      sourceFolder = "IC-201",
      deps = listOf("java", "org.jetbrains.plugins.gradle", "org.jetbrains.kotlin:1.3.72-release-IJ2020.1-5")
   ),
   plugin.PluginDescriptor(
      since = "202.1",
      until = "202.*",
      sdkVersion = "IC-2020.2",
      sourceFolder = "IC-202",
      deps = listOf("java", "org.jetbrains.plugins.gradle", "org.jetbrains.kotlin:1.4.10-release-IJ2020.2-1")
   ),
   plugin.PluginDescriptor(
      since = "203.5981.155", // this version is 2020.3.1 final
      until = "203.*",
      sdkVersion = "IC-2020.3",
      sourceFolder = "IC-203",
      deps = listOf("java", "org.jetbrains.plugins.gradle", "org.jetbrains.kotlin:1.4.10-release-IJ2020.2-1")
   ),
   plugin.PluginDescriptor(
      since = "211.4961.30", // this version is 2021.1 EAP
      until = "211.*",
      sdkVersion = "IC-211-EAP-SNAPSHOT",
      sourceFolder = "IC-211",
      deps = listOf("java", "org.jetbrains.plugins.gradle", "org.jetbrains.kotlin:211-1.4.21-release-IJ5538.2")
   )
)

val productName = System.getenv("PRODUCT_NAME") ?: System.getenv("SOURCE_FOLDER") ?: "IC-211"
val descriptor = plugins.first { it.sourceFolder == productName }

val jetbrainsToken: String by project

version = "1.1." + (System.getenv("GITHUB_RUN_NUMBER") ?: "0-SNAPSHOT")

intellij {
   sandboxDirectory = project.property("sandbox").toString()
   version = descriptor.sdkVersion
   pluginName = "kotest-plugin-intellij"
   setPlugins(*descriptor.deps.toTypedArray())
   downloadSources = true
   type = "IC"
   updateSinceUntilBuild = false
}

dependencies {
   compileOnly(kotlin("stdlib"))
   implementation("javax.xml.bind:jaxb-api:2.2.12")
   implementation("javax.activation:activation:1.1.1")

   // we bundle this for 4.1 support
   // in kotest 4.2.0 the launcher has moved to a stand alone module
   implementation("io.kotest:kotest-launcher:1.0.9")

   // this is needed to use the launcher in 4.2.0, in 4.2.1+ the launcher is built
   // into the engine dep which should already be on the classpath
   implementation("io.kotest:kotest-framework-launcher-jvm:4.2.0")

   // needed for the resource files which are loaded into java light tests
   testImplementation("io.kotest:kotest-framework-api:4.3.1")
   testImplementation("io.kotest:kotest-assertions-core-jvm:4.3.1")
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
         jvmTarget = "1.8"
      }
   }

   withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
      kotlinOptions {
         jvmTarget = "1.8"
      }
   }

   buildPlugin {
      archiveClassifier.set(descriptor.sdkVersion)
   }

   publishPlugin {
      token(System.getenv("JETBRAINS_TOKEN") ?: jetbrainsToken)
   }

   patchPluginXml {
      setVersion("${project.version}-${descriptor.sdkVersion}")
      setSinceBuild(descriptor.since)
      setUntilBuild(descriptor.until)
   }
}
