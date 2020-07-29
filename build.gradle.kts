import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

buildscript {
   repositories {
      mavenCentral()
   }
}

plugins {
   kotlin("jvm")
   java
   id("org.jetbrains.intellij").version("0.4.21")
}

repositories {
   mavenCentral()
   mavenLocal()
   maven("https://oss.sonatype.org/content/repositories/snapshots")
}

val plugins = listOf(
   plugin.PluginDescriptor(
      "193",
      "193.*",
      "IC-2019.3",
      listOf("java", "org.jetbrains.kotlin:1.3.72-release-IJ2019.3-5")
   ),
   plugin.PluginDescriptor(
      "201",
      "201.*",
      "IC-2020.1",
      listOf("java", "org.jetbrains.kotlin:1.3.72-release-IJ2020.1-5")
   ),
   plugin.PluginDescriptor(
      "202",
      "202.*",
      "IC-2020.2",
      listOf("java", "org.jetbrains.kotlin:1.3.72-release-IJ2020.1-5")
   ),
   plugin.PluginDescriptor(
      "191",
      "191.*",
      "Studio3.5",
      listOf("org.jetbrains.kotlin:1.3.30-release-Studio3.5-1")
   ),
   plugin.PluginDescriptor(
      "192",
      "192.*",
      "Studio3.6",
      listOf("android", "java", "org.jetbrains.kotlin:1.3.61-release-Studio3.6-1")
   ),
   plugin.PluginDescriptor(
      "193",
      "193.*",
      "Studio4.0",
      listOf("android", "java", "org.jetbrains.kotlin:1.3.70-release-Studio4.0-1")
   )
)

val sdkVersion = System.getenv("SDK_VERISON") ?: "IC-2020.2"
val sdk = plugins.first { it.version == sdkVersion }

val jetbrainsToken: String by project

version = "1.1." + (System.getenv("GITHUB_RUN_NUMBER") ?: "0-SNAPSHOT")

intellij {
   sandboxDirectory = project.property("sandbox").toString()
   version = sdk.version
   pluginName = "kotest-plugin-intellij"
   setPlugins(*sdk.deps.toTypedArray())
   downloadSources = true
   type = "IC"
   updateSinceUntilBuild = false
}

dependencies {
   compileOnly(kotlin("stdlib"))
   implementation("javax.xml.bind:jaxb-api:2.2.12")
   implementation("javax.activation:activation:1.1.1")
   implementation("io.kotest:kotest-launcher:1.0.5")
   testImplementation("io.kotest:kotest-assertions-core-jvm:4.1.3")
}

sourceSets {
   main {
      withConvention(KotlinSourceSet::class) {
         kotlin.srcDirs("src/${sdk.version}/kotlin")
      }
   }
}

tasks {
   compileKotlin {
      kotlinOptions {
         jvmTarget = "1.8"
      }
   }

   buildPlugin {
      archiveClassifier.set(sdk.version)
   }

   publishPlugin {
      token(System.getenv("JETBRAINS_TOKEN") ?: jetbrainsToken)
   }

   patchPluginXml {
      setVersion("${project.version}-${sdk.version}")
      setSinceBuild(sdk.since)
      setUntilBuild(sdk.until)
   }
}

//sourceCompatibility = 1.8
//targetCompatibility = 1.8
