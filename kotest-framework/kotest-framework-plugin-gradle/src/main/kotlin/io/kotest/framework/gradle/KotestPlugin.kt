package io.kotest.framework.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Property
import javax.inject.Inject

// gradle requires the class be extendable
open class KotestPlugin : Plugin<Project> {

   companion object {
      const val DESCRIPTION = "Run Kotest"
      const val TASK_NAME = "kotest"
      const val EXTENSION_NAME = "kotest"
      const val KOTLIN_JVM_PLUGIN = "org.jetbrains.kotlin.jvm"
   }

   override fun apply(project: Project) {

      // allows users to configure the test engine
      val extension = project.extensions.create(
         /* name = */ EXTENSION_NAME,
         /* type = */ KotestExtension::class.java,
         /* ...constructionArguments = */ project
      )

      // we only want to add the task if the project has the kotlin jvm plugin
      project.plugins.withId(KOTLIN_JVM_PLUGIN) {
         // gradle best practice is to only apply to this project, and users add the plugin to each subproject
         // see https://docs.gradle.org/current/userguide/isolated_projects.html
         project.tasks.register(TASK_NAME, KotestTask::class.java) {
            description = DESCRIPTION
            group = JavaBasePlugin.VERIFICATION_GROUP
            dependsOn(project.tasks.getByName(JavaPlugin.TEST_CLASSES_TASK_NAME))
         }
      }
   }
}

abstract class KotestExtension @Inject constructor(project: Project) {
   private val objects = project.objects
   val tags: Property<String> = objects.property(String::class.java)
}

