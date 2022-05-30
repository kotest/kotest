plugins {
   `java-library`
   kotlin("multiplatform")
}

kotlin {

   targets {

      jvm()

      js(IR) {
         browser()
         nodejs()
      }

      linuxX64()

      mingwX64()

      macosX64()
      macosArm64()

      tvos()
      tvosSimulatorArm64()

      watchosArm32()
      watchosArm64()
      watchosX86()
      watchosX64()
      watchosSimulatorArm64()

      iosX64()
      iosArm64()
      iosArm32()
      iosSimulatorArm64()
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            implementation(kotlin("reflect"))
            api(libs.kotlinx.coroutines.core)
//            implementation(Libs.Kotlin.kotlinScriptRuntime)
            implementation(project(Projects.Common))
            api(project(Projects.Assertions.Shared))
         }
      }

      val jsMain by getting {
         dependsOn(commonMain)
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            api(libs.kotlinx.coroutines.test)
         }
      }

      val desktopMain by creating {
         dependsOn(commonMain)
      }

      val macosX64Main by getting {
         dependsOn(desktopMain)
      }

      val macosArm64Main by getting {
         dependsOn(desktopMain)
      }

      val mingwX64Main by getting {
         dependsOn(desktopMain)
      }

      val linuxX64Main by getting {
         dependsOn(desktopMain)
      }

      val iosX64Main by getting {
         dependsOn(desktopMain)
      }

      val iosArm64Main by getting {
         dependsOn(desktopMain)
      }

      val iosArm32Main by getting {
         dependsOn(desktopMain)
      }

      val iosSimulatorArm64Main by getting {
         dependsOn(desktopMain)
      }

      val watchosArm32Main by getting {
         dependsOn(desktopMain)
      }

      val watchosArm64Main by getting {
         dependsOn(desktopMain)
      }

      val watchosX86Main by getting {
         dependsOn(desktopMain)
      }

      val watchosX64Main by getting {
         dependsOn(desktopMain)
      }

      val watchosSimulatorArm64Main by getting {
         dependsOn(desktopMain)
      }

      val tvosMain by getting {
         dependsOn(desktopMain)
      }

      val tvosSimulatorArm64Main by getting {
         dependsOn(desktopMain)
      }

      val jvmTest by getting {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Assertions.Core))
            // we use the internals of the JVM project in the tests
            implementation(project(Projects.JunitRunner))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.mockk)
            implementation(libs.junit.platform.engine)
            implementation(libs.junit.platform.api)
            implementation(libs.junit.platform.launcher)
            implementation(libs.junit.jupiter.api)
            // this is here to test that the intellij marker 'dummy' test doesn't appear in intellij
            implementation(libs.junit.jupiter.engine)
         }
      }

      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}

tasks.create("buildConfigDocs") {
   //find config files
   val fileNames = listOf("KotestEngineProperties.kt")

   val foundFiles = File(project.rootDir.absolutePath).walk().maxDepth(25).map { file ->
      if (fileNames.contains(file.name)) {
         file
      } else {
         null
      }
   }.filterNotNull()
      .toList()

   if (foundFiles.size != fileNames.size)
      throw RuntimeException("Fail to find files -> {$fileNames} in project, found only these files -> {$foundFiles}")

   //replace in docs

   val docName = "config_props.md"
   val docsFolder = File(project.rootDir.absolutePath, "documentation/docs/framework")
   val docFileFullPath = File(docsFolder.absolutePath, docName)

   val configTemplate = """
---
id: framework_config_props
title: Framework configuration properties
sidebar_label: System properties
slug: framework-config-props.html
---

   """.trimIndent()

   val fileTemplate = """

      ---
      #### %s
      ```kotlin
      %s
      ```

   """.trimIndent()

   val sb = StringBuilder(configTemplate)

   foundFiles.forEach { file ->
      val name = file.name
      // intentionally use \n instead of System.lineSeparator to respect .editorconfig
      val content = file.readLines().joinToString(separator = "\n")

      sb.append(fileTemplate.format(name, content))
   }

   docFileFullPath.writeText(sb.toString())
}

tasks["jvmTest"].mustRunAfter(tasks["buildConfigDocs"].path)

apply(from = "../../publish-mpp.gradle.kts")
