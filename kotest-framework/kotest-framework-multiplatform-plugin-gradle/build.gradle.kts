import org.gradle.api.tasks.PathSensitivity.RELATIVE
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import utils.SystemPropertiesArgumentProvider.Companion.SystemPropertiesArgumentProvider

plugins {
   `kotlin-dsl`
   id("kotest-publishing-conventions")
   alias(libs.plugins.gradle.plugin.publish)
}

group = "io.kotest"
version = Ci.gradleVersion

dependencies {
   compileOnly(libs.kotlin.gradle.plugin)

   testImplementation(libs.kotlin.gradle.plugin)
   testImplementation(projects.kotestAssertions.kotestAssertionsCore)
   testImplementation(projects.kotestFramework.kotestFrameworkApi)
   testImplementation(projects.kotestFramework.kotestFrameworkEngine)
   testImplementation(projects.kotestRunner.kotestRunnerJunit5)

   testImplementation(libs.mockk)

   devPublication(projects.kotestAssertions.kotestAssertionsApi)
   devPublication(projects.kotestAssertions.kotestAssertionsCore)
   devPublication(projects.kotestAssertions.kotestAssertionsShared)
   devPublication(projects.kotestExtensions)
   devPublication(projects.kotestFramework.kotestFrameworkDiscovery)
   devPublication(projects.kotestFramework.kotestFrameworkConcurrency)
   devPublication(projects.kotestCommon)
   devPublication(projects.kotestFramework.kotestFrameworkApi)
   devPublication(projects.kotestFramework.kotestFrameworkEngine)
   devPublication(projects.kotestFramework.kotestFrameworkMultiplatformPluginEmbeddableCompiler)
   devPublication(projects.kotestFramework.kotestFrameworkMultiplatformPluginLegacyNative)
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

   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.STANDARD_ERROR, TestLogEvent.STANDARD_OUT)
      exceptionFormat = TestExceptionFormat.FULL
   }
}

gradlePlugin {
   website.set("https://kotest.io")
   vcsUrl.set("https://github.com/kotest")
   plugins {
      create("KotestMultiplatformCompilerGradlePlugin") {
         id = "io.kotest.multiplatform"
         implementationClass = "io.kotest.framework.multiplatform.gradle.KotestMultiplatformCompilerGradlePlugin"
         displayName = "Kotest Multiplatform Compiler Plugin"
         description = "Adds support for Javascript and Native tests in Kotest"
         tags.set(listOf("kotest", "kotlin", "testing", "integrationtesting", "javascript"))
      }
   }
}

tasks.withType<KotlinCompile>().configureEach {
   kotlinOptions {
      compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
   }
}

tasks.withType<JavaCompile>().configureEach {
   options.release.set(8)
}

val updateKotestPluginConstants by tasks.registering {
   val kotestPluginConstants = """
      |// Generated file, do not edit manually
      |@file:org.gradle.api.Generated
      |
      |package io.kotest.framework.multiplatform.gradle
      |
      |const val KOTEST_COMPILER_PLUGIN_VERSION: String = "${Ci.gradleVersion}"
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
