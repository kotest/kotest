buildscript {
   repositories {
      mavenCentral()
      mavenLocal()
   }
}

plugins {
   java
   application
   id("kotlin")
   id("java-library")
   id("com.github.johnrengelman.shadow") version "7.1.0"
}

apply(plugin = "com.github.johnrengelman.shadow")

application {
   mainClassName = "io.kotest.engine.launcher.MainKt"
}

tasks {
   shadowJar {
      mergeServiceFiles()
      manifest {
         attributes(Pair("Main-Class", "io.kotest.engine.launcher.MainKt"))
      }
   }
}

dependencies {
   implementation(kotlin("stdlib"))
   implementation(kotlin("reflect"))
   api(project(Projects.Assertions.Shared))
   implementation(project(Projects.Common))
   implementation(project(Projects.Framework.engine))
   api(project(Projects.Framework.api))

//   api(Libs.Kotlin.kotlinScriptRuntime)
//   implementation(Libs.Kotlin.kotlinScriptUtil)
//   implementation(Libs.Kotlin.kotlinScriptJvm)
   implementation(Libs.Coroutines.test)
}

apply(from = "../../publish-mpp.gradle.kts")
