package io.kotest.plugin.intellij.gradle

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
      // if we have any kotest gradle task in the project, we assume the plugin is applied
      return kotestTasks(module).isNotEmpty()
   }

   @Suppress("UnstableApiUsage")
   fun listTasks(module: Module): List<GradleTaskData> {
      val externalProjectPath = resolveProjectPath(module) ?: return emptyList()
      return GradleTasksIndices.getInstance(module.project).findTasks(externalProjectPath)
   }

   /**
    * Returns true if the given task names contain any Kotest task.
    */
   fun hasKotestTask(taskNames: List<String>): Boolean {
      // tasks from the gradle plugin are like kotest, jsKotest, jvmKotest, wasmJsKotest, etc.
      // so returns true if any task in the project ends with kotest
      // todo really need some better way of identifying kotest tasks
      return taskNames.any { isKotestTaskName(it) }
   }

   @Suppress("UnstableApiUsage")
   fun kotestTasks(module: Module): List<GradleTaskData> {
      return listTasks(module)
         .filter { isKotestTaskName(it.name) }
         .sortedBy { it.name }
   }

   fun isKotestTaskName(taskName: String): Boolean {
      return taskName == "kotest" // jvm only task name
         || taskName.endsWith("Kotest") // thinks like linuxX84Kotest
         || taskName.endsWith("kotestDebugUnitTest") // android
         || taskName.endsWith("kotestReleaseUnitTest") // android
   }

   fun getDescriptorArg(taskNames: List<String>): String? {
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
}
