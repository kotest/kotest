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
   }

   override fun apply(project: Project) {

      // allows users to configure the test engine
      val extension = project.extensions.create(
         /* name = */ EXTENSION_NAME,
         /* type = */ KotestExtension::class.java,
         /* ...constructionArguments = */ project
      )

      // add a kotest task to all targets that have a test task
//      project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
//         println("multiplatform=" + this::class.qualifiedName)
//      }

      // todo only add to projects which have a Test type already
      // todo should only add to projects which have kotest on the classpath
      project.afterEvaluate {
         plugins.withId("org.jetbrains.kotlin.jvm") {
            this@afterEvaluate.allprojects.forEach { subproject ->
               subproject.tasks.register("kotest", KotestTask::class.java) {
                  description = DESCRIPTION
                  group = JavaBasePlugin.VERIFICATION_GROUP
                  dependsOn(subproject.tasks.getByName(JavaPlugin.TEST_CLASSES_TASK_NAME))
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

