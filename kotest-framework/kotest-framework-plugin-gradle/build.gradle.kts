import org.gradle.api.tasks.PathSensitivity.RELATIVE
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import utils.SystemPropertiesArgumentProvider.Companion.SystemPropertiesArgumentProvider
import java.nio.file.Files

plugins {
   `kotlin-dsl`
//   id("kotest-publishing-conventions")
   alias(libs.plugins.gradle.plugin.publish)
   id("com.github.node-gradle.node") version "7.1.0"
}

dependencies {
   compileOnly(libs.kotlin.gradle.plugin)
   implementation(libs.asm) // used to poke into classes to see if they are specs when running JVM tests
   testImplementation(libs.kotlin.gradle.plugin)
}

tasks.withType<Test>().configureEach {
   enabled = !project.hasProperty(Ci.JVM_ONLY)

   dependsOn(tasks.updateDevRepo)

   inputs.dir(devPublish.devMavenRepo)
      .withPropertyName("devPublish.devMavenRepo")
      .withPathSensitivity(RELATIVE)

   jvmArgumentProviders.add(
      SystemPropertiesArgumentProvider(
         devPublish.devMavenRepo.map { "devMavenRepoPath" to it.asFile.invariantSeparatorsPath }
      )
   )

   useJUnitPlatform()
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(FAILED, SKIPPED, STANDARD_ERROR, STANDARD_OUT)
      exceptionFormat = TestExceptionFormat.FULL
   }
}

gradlePlugin {
   isAutomatedPublishing = true
   website.set("https://kotest.io")
   vcsUrl.set("https://github.com/kotest")
   plugins {
      create("KotestGradlePlugin") {
         id = "io.kotest"
         implementationClass = "io.kotest.framework.gradle.KotestPlugin"
         displayName = "Kotest Gradle Plugin"
         description = "Adds support for running Kotest tests in gradle"
         tags.addAll("kotest", "kotlin", "testing", "integration testing")
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

tasks {
   val createPluginProperties = register("createPluginProperties") {
      group = "kotest"
      description = "Generates the version for the Kotest Gradle plugin"
      val propFile = project.layout.buildDirectory.file("generated/kotest.gradle.properties").get()
      outputs.file(propFile)
      doLast {
         mkdir(propFile.asFile.parentFile)
         Files.writeString(propFile.asFile.toPath(), "version=${project.version}")
      }
   }
   processResources {
      from(files(createPluginProperties))
   }
}
