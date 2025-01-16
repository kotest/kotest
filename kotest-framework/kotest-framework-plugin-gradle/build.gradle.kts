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
   implementation("org.ow2.asm:asm:9.7.1") // used to poke into classes to see if they are specs
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

@Suppress("UnstableApiUsage")
gradlePlugin {
   isAutomatedPublishing = true
   website.set("https://kotest.io")
   vcsUrl.set("https://github.com/kotest")
   plugins {
      create("KotestGradlePlugin") {
         id = "io.kotest"
         implementationClass = "io.kotest.framework.gradle.KotestPlugin"
         displayName = "Kotest Multiplatform Compiler Plugin"
         description = "Adds support for JavaScript and Native tests in Kotest"
         tags.addAll("kotest", "kotlin", "testing", "integration testing", "javascript", "native")
      }
   }
}

tasks.withType<KotlinCompile>().configureEach {
   kotlin {
      compilerOptions {
         jvmTarget.set(JvmTarget.JVM_1_8)
      }
   }
}

tasks.withType<JavaCompile>().configureEach {
   options.release.set(8)
}
