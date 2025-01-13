package io.kotest.framework.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import javax.inject.Inject

// gradle requires the class be extendable
open class KotestPlugin : Plugin<Project> {

   companion object {
      const val DESCRIPTION = "Run Kotest"
      const val TASK_NAME = "kotest"
      const val EXTENSION_NAME = "kotest"
   }

   override fun apply(project: Project) {

      val extension = project.extensions.create(
         /* name = */ EXTENSION_NAME,
         /* type = */ KotestExtension::class.java,
         /* ...constructionArguments = */ project
      )

      // we only add the kotest task for subprojects that have the kotest dependencies on the classpath
//      project.allprojects.forEach {
//         val ext = it.extensions.findByType(KotlinJvmProjectExtension::class.java)
//         println(it.name + " " + ext)
//      }

      // add a kotest task to all targets that have a test task
      project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
         println("multiplatform=" + this::class.qualifiedName)
      }

      project.plugins.withId("org.jetbrains.kotlin.jvm") {
         when (this) {
            is KotlinPluginWrapper -> {
               project.tasks.withType(Test::class.java).forEach { task ->
                  // todo should only add to projects which have kotest on the classpath
                  project.allprojects.forEach { subproject ->
                     println("Adding kotest task to ${subproject.name}")
                     val ext = project.extensions.findByType(JavaPluginExtension::class.java)
                     applyPlugin(
                        subproject,
                        task.name.replace("test", "kotest").replace("Test", "Kotest"),
                        JavaPlugin.TEST_CLASSES_TASK_NAME,
                     )
                  }
               }
            }
         }
      }
   }

   private fun applyPlugin(project: Project, taskName: String, dependentTask: String?) {
      if (project.tasks.none { it.name == taskName }) {

         val task = project.tasks.maybeCreate(taskName, KotestTask::class.java)
         task.description = DESCRIPTION
         task.group = JavaBasePlugin.VERIFICATION_GROUP

         if (dependentTask != null && project.tasks.any { it.name == dependentTask })
            task.dependsOn(dependentTask)
         project.subprojects.forEach { applyPlugin(it, taskName, dependentTask) }
      }
   }
}

abstract class KotestExtension @Inject constructor(project: Project) {

   private val objects = project.objects

   val tags: Property<String> = objects.property(String::class.java)
}

