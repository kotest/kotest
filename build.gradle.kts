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
      "193.4099.13",
      "193.*",
      "IC-2019.3",
      "IC-2019.3",
      listOf("java", "org.jetbrains.kotlin:1.3.72-release-IJ2019.3-5")
   ),
   plugin.PluginDescriptor(
      "201.6487",
      "202.*",
      "IC-2020.1",
      "IC-2020.1",
      listOf("java", "org.jetbrains.kotlin:1.3.72-release-IJ2020.1-5")
   ),
   plugin.PluginDescriptor(
      "193.5233.102",
      "193.*",
      "193.5233.102",
      "AS-4.0",
      listOf("gradle", "android", "java", "org.jetbrains.kotlin:1.3.72-release-Studio4.0-5")
   ),
   plugin.PluginDescriptor(
      "201.7223.91",
      "201.*",
      "201.7223.91",
      "AS-4.1",
      listOf("gradle", "android", "java", "org.jetbrains.kotlin:1.3.72-release-Studio4.1-5")
   )
)

val productName = System.getenv("PRODUCT_NAME") ?: "IC-2020.1"
val descriptor = plugins.first { it.productName == productName }

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
   implementation("io.kotest:kotest-launcher:1.0.6")
   testImplementation("io.kotest:kotest-assertions-core-jvm:4.1.3")
}

sourceSets {
   main {
      withConvention(KotlinSourceSet::class) {
         kotlin.srcDirs("src/${descriptor.productName}/kotlin")
      }
      resources {
         srcDir("src/${descriptor.productName}/resources")
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
