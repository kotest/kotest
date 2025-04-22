import org.gradle.api.tasks.PathSensitivity.RELATIVE
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import utils.SystemPropertiesArgumentProvider.Companion.SystemPropertiesArgumentProvider

plugins {
   `kotlin-dsl`
   id("kotest-publishing-conventions")
   alias(libs.plugins.gradle.plugin.publish)
}

dependencies {
   compileOnly(libs.kotlin.gradle.plugin)

   testImplementation(libs.kotlin.gradle.plugin)
   testImplementation(projects.kotestAssertions.kotestAssertionsCore)
   testImplementation(projects.kotestFramework.kotestFrameworkEngine)
   testImplementation(projects.kotestRunner.kotestRunnerJunit5)

   testImplementation(libs.mockk)

   devPublication(projects.kotestAssertions.kotestAssertionsCore)
   devPublication(projects.kotestAssertions.kotestAssertionsShared)
   devPublication(projects.kotestExtensions)
   devPublication(projects.kotestCommon)
   devPublication(projects.kotestFramework.kotestFrameworkEngine)
   devPublication(projects.kotestFramework.kotestFrameworkMultiplatformPluginCompiler)
   devPublication(projects.kotestRunner.kotestRunnerJunit5)
}

tasks.withType<Test>().configureEach {
   enabled = !project.hasProperty(Ci.JVM_ONLY)

   //region Configure devMavenRepo
   dependsOn(tasks.updateDevRepo)

   inputs.dir(devPublish.devMavenRepo)
      .withPropertyName("devPublish.devMavenRepo")
      .withPathSensitivity(RELATIVE)

   jvmArgumentProviders.add(
      SystemPropertiesArgumentProvider(
         devPublish.devMavenRepo.map { "devMavenRepoPath" to it.asFile.invariantSeparatorsPath }
      )
   )
   //endregion

   useJUnitPlatform()

   systemProperty("kotestVersion", Ci.publishVersion)

   //region pass test-project directory as system property
   val testProjectDir = layout.projectDirectory.dir("test-project")
   inputs.dir(testProjectDir)
      .withPropertyName("testProjectDir")
      .withPathSensitivity(RELATIVE)
   systemProperty("testProjectDir", testProjectDir.asFile.invariantSeparatorsPath)
   //endregion

   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(FAILED, SKIPPED, STANDARD_ERROR, STANDARD_OUT)
      exceptionFormat = TestExceptionFormat.FULL
   }
}

@Suppress("UnstableApiUsage")
gradlePlugin {
   isAutomatedPublishing = true
   website.set("https://kotest.io")
   vcsUrl.set("https://github.com/kotest")
   plugins {
      create("KotestMultiplatformCompilerGradlePlugin") {
         id = "io.kotest.multiplatform"
         implementationClass = "io.kotest.framework.multiplatform.gradle.KotestMultiplatformCompilerGradlePlugin"
         displayName = "Kotest Multiplatform Compiler Plugin"
         description = "Adds support for JavaScript and Native tests in Kotest"
         tags.addAll("kotest", "kotlin", "testing", "integration testing", "javascript", "native")
      }
   }
}

tasks.withType<KotlinCompile>().configureEach {
   kotlin {
      compilerOptions {
         jvmTarget.set(JvmTarget.JVM_11)
      }
   }
}

tasks.withType<JavaCompile>().configureEach {
   options.release.set(11)
}

val updateKotestPluginConstants by tasks.registering {
   val kotestPluginConstants = """
      |// Generated file, do not edit manually
      |@file:org.gradle.api.Generated
      |
      |package io.kotest.framework.multiplatform.gradle
      |
      |const val KOTEST_COMPILER_PLUGIN_VERSION: String = "${Ci.publishVersion}"
      |
   """.trimMargin()

   inputs.property("kotestPluginConstants", kotestPluginConstants)

   val outputDir = layout.buildDirectory.dir("generated/src/main/kotlin/")
   outputs.dir(outputDir).withPropertyName("outputDir")

   doLast {
      val kotestPluginConstantsFile = outputDir.get().asFile
         .resolve("io/kotest/framework/multiplatform/gradle/kotestPluginConstants.kt")

      kotestPluginConstantsFile.apply {
         parentFile.deleteRecursively()
         parentFile.mkdirs()
         writeText(kotestPluginConstants)
      }
   }
}

kotlin.sourceSets.main {
   kotlin.srcDir(updateKotestPluginConstants)
}

tasks.clean.configure {
   delete("${project.layout.projectDirectory}/test-project/build/")
   delete("${project.layout.projectDirectory}/test-project/.gradle/")
}
