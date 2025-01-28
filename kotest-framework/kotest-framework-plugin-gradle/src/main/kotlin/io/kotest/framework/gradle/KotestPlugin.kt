package io.kotest.framework.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// gradle requires the class be extendable
open class KotestPlugin : Plugin<Project> {

   companion object {
      const val DESCRIPTION = "Runs tests using Kotest"
      const val JVM_TASK_NAME = "kotest"
      const val EXTENSION_NAME = "kotest"

      private const val KOTLIN_JVM_PLUGIN = "org.jetbrains.kotlin.jvm"
      private const val KOTLIN_MULTIPLATFORM_PLUGIN = "org.jetbrains.kotlin.multiplatform"
      private const val KOTLIN_ANDROID_PLUGIN = "org.jetbrains.kotlin.android"

      private val unsupportedTargets = listOf(
         "metadata"
      )
   }

   override fun apply(project: Project) {

      // allows users to configure the test engine
      val extension = project.extensions.create(
         /* name = */ EXTENSION_NAME,
         /* type = */ KotestExtension::class.java,
         /* ...constructionArguments = */ project
      )

      // Configure Kotlin JVM projects
      project.plugins.withId(KOTLIN_JVM_PLUGIN) {
         // gradle best practice is to only apply to this project, and users add the plugin to each subproject
         // see https://docs.gradle.org/current/userguide/isolated_projects.html
         project.tasks.register(JVM_TASK_NAME, KotestJvmTask::class.java) {
            description = DESCRIPTION
            group = JavaBasePlugin.VERIFICATION_GROUP
            inputs.files(project.tasks.withType<KotlinCompile>().map { it.outputs.files })
         }
      }

      // Configure Kotlin multiplatform projects
      project.plugins.withId(KOTLIN_MULTIPLATFORM_PLUGIN) {
         project.extensions.configure<KotlinMultiplatformExtension> {
            targets.configureEach {
               if (name !in unsupportedTargets) {
                  val capitalTarget = name.replaceFirstChar { it.uppercase() }
                  // gradle best practice is to only apply to this project, and users add the plugin to each subproject
                  // see https://docs.gradle.org/current/userguide/isolated_projects.html
                  project.tasks.register("kotest$capitalTarget", AbstractKotestTask::class.java) {
                     description = DESCRIPTION
                     group = JavaBasePlugin.VERIFICATION_GROUP
                     inputs.files(project.tasks.named("${name}TestClasses").map { it.outputs.files })
                  }
               }
            }
         }
      }

      // Configure Kotlin Android projects
      project.plugins.withId(KOTLIN_ANDROID_PLUGIN) {
         project.extensions.configure<KotlinAndroidExtension> {

            // todo better way to detect the test compilations, or find a way to get android variants
            // by default will be debugUnitTest and releaseUnitTest
            val testCompilations = target.compilations.matching { it.name.endsWith("UnitTest") }

            testCompilations.configureEach {
               val compilation: KotlinCompilation<*> = this
               val capitalTarget = name.replaceFirstChar { it.uppercase() }
               // gradle best practice is to only apply to this project, and users add the plugin to each subproject
               // see https://docs.gradle.org/current/userguide/isolated_projects.html
               project.tasks.register("kotest$capitalTarget", KotestAndroidTask::class.java) {
                  description = DESCRIPTION
                  group = JavaBasePlugin.VERIFICATION_GROUP
                  compilationNames.set(listOf(compilation.name))
                  inputs.files(project.tasks.withType<KotlinCompile>().map { it.outputs.files })
               }
            }

            // add one special task that runs all compilations
            // todo can we just make a task that runs the other tasks above
            project.tasks.register("kotest", KotestAndroidTask::class.java) {
               description = DESCRIPTION
               group = JavaBasePlugin.VERIFICATION_GROUP
               compilationNames.set(testCompilations.map { it.name })
               inputs.files(project.tasks.withType<KotlinCompile>().map { it.outputs.files })
            }
         }
      }
   }
}

