package io.kotest.plugin.intellij.gradle

import com.intellij.openapi.module.Module
import io.kotest.plugin.intellij.Constants
import org.jetbrains.plugins.gradle.execution.GradleRunnerUtil
import org.jetbrains.plugins.gradle.execution.build.CachedModuleDataFinder
import org.jetbrains.plugins.gradle.service.project.GradleTasksIndices
import org.jetbrains.plugins.gradle.util.GradleModuleData

object GradleUtils {

   /**
    * Returns true if we have the kotest gradle plugin configured for the given module.
    */
   @Suppress("UnstableApiUsage")
   fun hasGradlePlugin(module: Module?): Boolean {
      if (module == null) return false
      val externalProjectPath = resolveProjectPath(module) ?: return false
      return GradleTasksIndices.getInstance(module.project)
         .findTasks(externalProjectPath)
         .any { it.name.endsWith(Constants.KOTEST_GRADLE_TASK_PREFIX) }
   }

   fun hasKotestTask(taskNames: List<String>): Boolean {
      return taskNames.any { it.startsWith(Constants.KOTEST_GRADLE_TASK_PREFIX) }
   }

   fun resolveProjectPath(module: Module): String? {
      val gradleModuleData: GradleModuleData = CachedModuleDataFinder.getGradleModuleData(module) ?: return null
      val isGradleProjectDirUsedToRunTasks = gradleModuleData.directoryToRunTask == gradleModuleData.gradleProjectDir
      if (!isGradleProjectDirUsedToRunTasks) {
         return gradleModuleData.directoryToRunTask
      }
      return GradleRunnerUtil.resolveProjectPath(module)
   }
}
