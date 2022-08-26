import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
   kotlin("jvm")
   `maven-publish`
   `java-gradle-plugin`
   `kotlin-dsl`
   alias(libs.plugins.gradle.plugin.publish)
}

group = "io.kotest"
version = Ci.gradleVersion

java {
   sourceCompatibility = JavaVersion.VERSION_1_8
   targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
   mavenCentral()
   mavenLocal()
}

dependencies {
   implementation(libs.kotlin.gradle.plugin)

   testImplementation(project(Projects.Assertions.Core))
   testImplementation(project(Projects.Framework.api))
   testImplementation(project(Projects.Framework.engine))
   testImplementation(project(Projects.JunitRunner))

   testImplementation(libs.mockk)
}

tasks.withType<Test>().configureEach {
   // Build these libraries ahead of time so that the test project doesn't try to build them itself (if it tries to build them while we are as well, this can lead to conflicts)
   setOf(
      Projects.Assertions.Core,
      Projects.Framework.api,
      Projects.Framework.engine
   ).forEach { project ->
      setOf(
         "jvmJar",
         "compileKotlinLinuxX64",
         "compileKotlinMacosX64",
         "compileKotlinMacosArm64",
         "compileKotlinMingwX64",
      ).forEach { task ->
         dependsOn("$project:$task")
      }
   }

   setOf(
      Projects.JunitRunner,
      ":kotest-framework:kotest-framework-multiplatform-plugin-embeddable-compiler",
      ":kotest-framework:kotest-framework-multiplatform-plugin-legacy-native"
   ).forEach { project ->
      dependsOn("$project:jvmJar")
   }

   dependsOn("jar")
   dependsOn(":kotlinNpmInstall")

   useJUnitPlatform()

   systemProperty("kotestVersion", Ci.publishVersion)

   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.STANDARD_ERROR, TestLogEvent.STANDARD_OUT)
      exceptionFormat = TestExceptionFormat.FULL
   }
}


pluginBundle {
   website = "https://kotest.io"
   vcsUrl = "https://github.com/kotest"
   tags = listOf("kotest", "kotlin", "testing", "integrationTesting", "javascript")
}


gradlePlugin {
   plugins {
      create("KotestMultiplatformCompilerGradlePlugin") {
         id = "io.kotest.multiplatform"
         implementationClass = "io.kotest.framework.multiplatform.gradle.KotestMultiplatformCompilerGradlePlugin"
         displayName = "Kotest Multiplatform Compiler Plugin"
         description = "Adds support for Javascript and Native tests in Kotest"
      }
   }
}


val kotestPluginConstantsFileContents = resources.text.fromString(
   """
      |// Generated file, do not edit manually
      |@file:org.gradle.api.Generated
      |
      |package io.kotest.framework.multiplatform.gradle
      |
      |const val KOTEST_COMPILER_PLUGIN_VERSION: String = "${Ci.gradleVersion}"
      |
   """.trimMargin()
)

val updateKotestPluginConstants by tasks.registering(Sync::class) {

   from(kotestPluginConstantsFileContents) {
      rename { "kotestPluginConstants.kt" }
      into("io/kotest/framework/multiplatform/gradle/")
   }
   into(layout.buildDirectory.dir("generated/src/main/kotlin/"))

   doFirst {
      logger.debug(
         """
            Updating Kotest Gradle plugin constants
            ${kotestPluginConstantsFileContents.asString().prependIndent("  > ")}
         """.trimIndent()
      )
   }
}


sourceSets.main {
   java.srcDir(updateKotestPluginConstants.map { it.destinationDir })
}


tasks.clean {
   delete("$projectDir/test-project/build/")
   delete("$projectDir/test-project/.gradle/")
}
