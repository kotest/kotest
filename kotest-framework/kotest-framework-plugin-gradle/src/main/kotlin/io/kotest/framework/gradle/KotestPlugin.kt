package io.kotest.framework.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import javax.inject.Inject

// gradle requires the class be extendable
open class KotestPlugin : Plugin<Project> {

   companion object {
      const val DESCRIPTION = "Run Kotest"
      const val TASK_NAME = "kotest"
      const val EXTENSION_NAME = "kotest"

      private const val KOTLIN_JVM_PLUGIN = "org.jetbrains.kotlin.jvm"
      private const val KOTLIN_MULTIPLATFORM_PLUGIN = "org.jetbrains.kotlin.multiplatform"
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
      project.pluginManager.withPlugin(KOTLIN_JVM_PLUGIN) {
         project.extensions.configure<KotlinJvmExtension> {
            project.tasks.register("kotest", KotestTask::class.java) {
               description = DESCRIPTION
               group = JavaBasePlugin.VERIFICATION_GROUP
               inputs.files(project.tasks.withType<KotlinCompile>().map { it.outputs.files })
               // TODO: I think wiring dependencies,
//               dependsOn(project.tasks.withType<KotlinCompile>())
            }
         }
      }

      // Configure Kotlin multiplatform projects
      project.pluginManager.withPlugin(KOTLIN_MULTIPLATFORM_PLUGIN) {
         project.extensions.configure<KotlinMultiplatformExtension> {
            targets.configureEach {
               println("Detected target: $name")
               if (name in unsupportedTargets) {
                  println("Skipping unsupported target: $name")
               } else {
                  val capitalTarget = name.replaceFirstChar { it.uppercase() }
                  println("Creating task kotest$capitalTarget")
                  project.tasks.register("kotest$capitalTarget", KotestTask::class.java) {
                     description = DESCRIPTION
                     group = JavaBasePlugin.VERIFICATION_GROUP
                     println("Adding dependency on test classes")
                     inputs.files(project.tasks.named("${name}TestClasses").map { it.outputs.files })
//                     dependsOn(project.tasks.named("${targetName}TestClasses"))
                     // TODO: Setup dependency on the compile task for this target's test source set
                  }
               }
            }
         }
      }
   }
}

abstract class KotestExtension @Inject constructor(project: Project) {
   private val objects = project.objects
   val tags: Property<String> = objects.property(String::class.java)
}

