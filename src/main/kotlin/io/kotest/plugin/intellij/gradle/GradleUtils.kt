package io.kotest.plugin.intellij.gradle

import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.module.Module
import io.kotest.plugin.intellij.run.GradleTaskNamesBuilder
import org.jetbrains.plugins.gradle.execution.GradleRunnerUtil
import org.jetbrains.plugins.gradle.execution.build.CachedModuleDataFinder
import org.jetbrains.plugins.gradle.service.project.GradleTasksIndices
import org.jetbrains.plugins.gradle.util.GradleModuleData
import org.jetbrains.plugins.gradle.util.GradleTaskData

object GradleUtils {

   /**
    * Returns true if we have the Kotest Gradle plugin configured for the given module.
    */
   @Suppress("UnstableApiUsage")
   fun hasGradlePlugin(module: Module?): Boolean {
      if (module == null) return false
//      GradleSettings.getInstance(module.project).linkedProjectsSettings.forEach { settings ->
//         val gm = ExternalSystemApiUtil.getManager() as GradleManager
//         gm.
//      }
      // if we have any kotest gradle task in the project, we assume the plugin is applied
      return listTasks(module).any { isKotestTaskName(it.name) }
   }

   @Suppress("UnstableApiUsage")
   fun listTasks(module: Module): List<GradleTaskData> {
      val modulePath = resolveModulePath(module) ?: return emptyList()
      val moduleData = moduleData(module) ?: return emptyList()
      // this will return tasks like :kotest and :mymodule:kotest, so we need to filter them
      val tasks = GradleTasksIndices.getInstance(module.project).findTasks(modulePath)
      // filter down the tasks to the module only
      return tasks.filter { it.getFqnTaskName().startsWith(moduleData.moduleData.id + ":") }
   }

   /**
    * Returns true if any of the given [taskNames] are are the 'kotest' task.
    */
   fun hasKotestTask(taskNames: List<String>): Boolean {
      return taskNames.any { isKotestTaskName(it) }
   }

   fun isKotestTaskName(taskName: String): Boolean {
      return taskName == "kotest" // jvm only task name
         || taskName == "jvmKotest" // multiplatform jvm task name
         || taskName.matches("kotest[a-zA-Z]+UnitTest".toRegex()) // android task names eg kotestReleaseUnitTest, kotestDebugUnitTest, etc.
   }


   fun getIncludeArg(taskNames: List<String>): String? {
      val arg = taskNames.firstOrNull { it.startsWith(GradleTaskNamesBuilder.PROPERTY_INCLUDE) } ?: return null
      return arg.substringAfter(GradleTaskNamesBuilder.PROPERTY_INCLUDE).trim().removePrefix("=").removeSurrounding("'")
   }

   @Suppress("UnstableApiUsage")
   fun resolveProjectPath(module: Module): String? {
      val gradleModuleData: GradleModuleData = CachedModuleDataFinder.getGradleModuleData(module) ?: return null
      val isGradleProjectDirUsedToRunTasks = gradleModuleData.directoryToRunTask == gradleModuleData.gradleProjectDir
      if (!isGradleProjectDirUsedToRunTasks) {
         return gradleModuleData.directoryToRunTask
      }
      return GradleRunnerUtil.resolveProjectPath(module)
   }

   fun resolveModulePath(module: Module): String? {
      return ExternalSystemApiUtil.getExternalProjectPath(module)
   }

   @Suppress("UnstableApiUsage")
   fun moduleData(module: Module): GradleModuleData? {
      return CachedModuleDataFinder.getGradleModuleData(module)
   }
}
