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
   @Suppress("UnstableApiUsage")
   fun hasGradlePlugin(module: Module?): Boolean {
      if (module == null) return false
      // if we have any kotest task in the project, we assume the plugin is applied
      return hasKotestTask(listTasks(module).map { it.name })
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
      return taskNames.any { it.lowercase().endsWith("kotest") }
   }

   fun getDescriptorArg(taskNames: List<String>): String? {
      val arg = taskNames.firstOrNull { it.startsWith(GradleTaskNamesBuilder.DESCRIPTOR_ARG) } ?: return null
      return arg.substringAfter(GradleTaskNamesBuilder.DESCRIPTOR_ARG).trim().removeSurrounding("'")
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
