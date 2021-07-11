plugins {
   id("java")
   id("kotlin-multiplatform")
   id("java-library")
   id("com.adarshr.test-logger")
}

repositories {
   mavenCentral()
}

kotlin {

   targets {
      jvm {
         compilations.all {
            kotlinOptions {
               jvmTarget = "1.8"
            }
         }
      }
      js(BOTH) {
         browser()
         nodejs()
      }
      linuxX64()
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            implementation(kotlin("reflect"))
            implementation(Libs.Coroutines.coreCommon)
            implementation(Libs.Kotlin.kotlinScriptRuntime)
            implementation(project(Projects.Common))
            api(project(Projects.AssertionsShared))
         }
      }

      val jsMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(Libs.Coroutines.coreJs)
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(Libs.Coroutines.coreJvm)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(project(Projects.Engine))
            implementation(project(Projects.AssertionsCore))
            // we use the internals of the JVM project in the tests
            implementation(project(Projects.JunitRunner))
            implementation(Libs.Coroutines.coreJvm)
            implementation(Libs.Mocking.mockk)
            implementation(Libs.JUnitPlatform.engine)
            implementation(Libs.JUnitPlatform.api)
            implementation(Libs.JUnitPlatform.launcher)
            implementation(Libs.JUnitJupiter.api)
            // this is here to test that the intellij marker 'dummy' test doesn't appear in intellij
            implementation(Libs.JUnitJupiter.engine)
         }
      }

      val desktopMain by creating {
         dependsOn(commonMain)
      }

      val linuxX64Main by getting {
         dependsOn(desktopMain)
      }

      all {
         languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
         languageSettings.useExperimentalAnnotation("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions.jvmTarget = "1.8"
   kotlinOptions.apiVersion = "1.5"
}

tasks.named<Test>("jvmTest") {
   useJUnitPlatform()
   filter {
      isFailOnNoMatchingTests = false
   }
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED, org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED)
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
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
      val content = file.readLines().joinToString(separator = System.lineSeparator())

      sb.append(fileTemplate.format(name, content))
   }

   docFileFullPath.writeText(sb.toString())
}

tasks["jvmTest"].mustRunAfter(tasks["buildConfigDocs"].path)

apply(from = "../../publish-mpp.gradle.kts")
