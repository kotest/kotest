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
   fun hasGradlePlugin(module: Module?): Boolean {
      if (module == null) return false
//      GradleSettings.getInstance(module.project).linkedProjectsSettings.forEach { settings ->
//         val gm = ExternalSystemApiUtil.getManager() as GradleManager
//         gm.
//      }
      // if we have any kotest gradle task in the project, we assume the plugin is applied
      return kotestTasks(module).isNotEmpty()
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
    * Returns true if the given task names contain any Kotest task.
    */
   fun hasKotestTask(taskNames: List<String>): Boolean {
      // tasks from the gradle plugin are like kotest, jsKotest, jvmKotest, wasmJsKotest, etc.
      // todo really need some better way of identifying kotest tasks
      return taskNames.any { isKotestTaskName(it) }
   }

   /**
    * Returns a list of Kotest tasks for the given module.
    */
   @Suppress("UnstableApiUsage")
   fun kotestTasks(module: Module): List<GradleTaskData> {
      return listTasks(module)
         .filter { isKotestTaskName(it.name) }
         .sortedBy { it.name }
   }

   fun isKotestTaskName(taskName: String): Boolean {
      return taskName == "kotest" // jvm only task name
         || taskName == "jvmKotest" // multiplatform jvm task name
         || taskName.matches("kotest[a-zA-Z]+UnitTest".toRegex()) // android task names eg kotestReleaseUnitTest, kotestDebugUnitTest, etc.
   }

   fun getIncludeArg(taskNames: List<String>): String? {
      val arg = taskNames.firstOrNull { it.startsWith(GradleTaskNamesBuilder.ARG_INCLUDE) } ?: return null
      return arg.substringAfter(GradleTaskNamesBuilder.ARG_INCLUDE).trim().removeSurrounding("'")
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
