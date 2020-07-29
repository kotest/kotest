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
   )
)

val sdkVersion = project.properties["sdk.version"] ?: "IC-2020.2"
val sdk = plugins.first { it.version == sdkVersion }

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

   patchPluginXml {
      setVersion("${project.version}-${sdk.version}")
      setSinceBuild(sdk.since)
      setUntilBuild(sdk.until)
   }
}

//sourceCompatibility = 1.8
//targetCompatibility = 1.8

//publishPlugin {
//   token(jetbrainsToken)
//}
