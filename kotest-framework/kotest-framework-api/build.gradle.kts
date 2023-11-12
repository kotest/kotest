plugins {
   id("kotest-multiplatform-library-conventions")
   id("kotest-android-native-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            api(libs.kotlinx.coroutines.core)
            api(projects.kotestAssertions.kotestAssertionsShared)
            implementation(kotlin("reflect"))
            api(projects.kotestCommon) // needs to be API so the domain objects are open
            api(libs.kotlinx.coroutines.test)
         }
      }

      val jvmMain by getting {
         dependencies {
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
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
   }
}

tasks.create("buildConfigDocs") {
   // find config files
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

   // replace in docs

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
