import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
   compileOnly(gradleApi())
   compileOnly(libs.kotlin.gradle.plugin)

   testImplementation(project(Projects.Assertions.Core))
   testImplementation(project(Projects.Framework.api))
   testImplementation(project(Projects.Framework.engine))
   testImplementation(project(Projects.JunitRunner))
}

tasks.withType<Test>().configureEach {
   useJUnitPlatform()

   systemProperty("kotestVersion", Ci.publishVersion)
   val gradleWrapper = if ("windows" in System.getProperty("os.name").toLowerCase()) {
      "gradlew.bat"
   } else {
      "gradlew"
   }
   systemProperty(
      "gradleWrapper",
      rootProject.layout.projectDirectory.file(gradleWrapper).asFile.canonicalPath
   )

   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.STANDARD_ERROR, TestLogEvent.STANDARD_OUT)
      exceptionFormat = TestExceptionFormat.FULL
   }
}

tasks {
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
}


val updateKotestPluginConstants by tasks.registering {
   val kotestConstantsFileContent: String = """
            |// Do not edit manually. This file was created by ${this.path}
            |
            |package io.kotest.framework.multiplatform.gradle
            |
            |const val KOTEST_COMPILER_PLUGIN_VERSION: String = "${Ci.gradleVersion}"
            |
         """.trimMargin()
   inputs.property("kotestConstantsFileContent", kotestConstantsFileContent)

   val kotestConstantsOutputFile = project.layout.projectDirectory.file(
      "src/main/kotlin/io/kotest/framework/multiplatform/gradle/kotestPluginConstants.kt"
   )
   outputs.file(kotestConstantsOutputFile)

   doLast {
      logger.lifecycle("Updating Kotest Gradle plugin constants\n${kotestConstantsFileContent.indexOf("  > ")}\n")
      kotestConstantsOutputFile.asFile.writeText(
         kotestConstantsFileContent.lines().joinToString("\n")
      )
   }
}


tasks.withType<KotlinCompile>().configureEach {
   dependsOn(updateKotestPluginConstants)
}


tasks.clean {
   delete("$projectDir/test-project/build/")
   delete("$projectDir/test-project/.gradle/")
}
