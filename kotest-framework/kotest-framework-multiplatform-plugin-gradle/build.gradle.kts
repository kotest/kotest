import org.gradle.api.tasks.PathSensitivity.RELATIVE
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import utils.SystemPropertiesArgumentProvider.Companion.SystemPropertiesArgumentProvider

plugins {
   `kotlin-dsl`
   id("kotest-publishing-conventions")
   alias(libs.plugins.gradle.plugin.publish)
   `java-test-fixtures`
}

dependencies {
   compileOnly(libs.kotlin.gradle.plugin)

   testImplementation(libs.kotlin.gradle.plugin)
   testImplementation(projects.kotestAssertions.kotestAssertionsCore)
   testImplementation(projects.kotestFramework.kotestFrameworkApi)
   testImplementation(projects.kotestFramework.kotestFrameworkEngine)
   testImplementation(projects.kotestRunner.kotestRunnerJunit5)

   testImplementation(libs.mockk)

   devPublication(projects.kotestAssertions.kotestAssertionsCore)
   devPublication(projects.kotestAssertions.kotestAssertionsShared)
   devPublication(projects.kotestExtensions)
   devPublication(projects.kotestFramework.kotestFrameworkDiscovery)
   devPublication(projects.kotestCommon)
   devPublication(projects.kotestFramework.kotestFrameworkApi)
   devPublication(projects.kotestFramework.kotestFrameworkEngine)
   devPublication(projects.kotestFramework.kotestFrameworkMultiplatformPluginEmbeddableCompiler)
   devPublication(projects.kotestFramework.kotestFrameworkMultiplatformPluginLegacyNative)
   devPublication(projects.kotestRunner.kotestRunnerJunit5)
}

kotlin {
   compilerOptions {
      optIn.add("kotlin.io.path.ExperimentalPathApi")
   }
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

   // use the current machine's Gradle User Home as a read-only cache
   systemProperty("gradleUserHomeDir", gradle.gradleUserHomeDir.invariantSeparatorsPath)

   // store the Gradle logs in a project-local directory, so they're easy to view if a test fails
   systemProperty("testLogDir", temporaryDir.resolve("logs").invariantSeparatorsPath)

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

   systemProperty("kotest.framework.classpath.scanning.autoscan.disable", "true")
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
   delete(
      layout.projectDirectory.dir("test-project/build"),
      layout.projectDirectory.dir("test-project/.gradle"),
   )
}

// Don't publish test fixtures (we don't need to share them, and they cause warnings when publishing)
// https://docs.gradle.org/current/userguide/java_testing.html#publishing_test_fixtures
val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }

//region TODO investigate bug where io.kotest.multiplatform Gradle Plugin Marker isn't published to dev-maven
//            Maybe need to update dev.publish to always re-publish if there are no artifacts?
tasks.withType<dev.adamko.gradle.dev_publish.tasks.BaseDevPublishTask>().configureEach {
   outputs.upToDateWhen { false }
}

tasks.withType<AbstractPublishToMaven>().configureEach {
   outputs.upToDateWhen { false }
}

tasks.generatePublicationHashTask {
   publicationData.matching { it.name == "KotestMultiplatformCompilerGradlePluginPluginMarkerMaven" }.configureEach {
      identifier.set("hack")
   }
}
//endregion
