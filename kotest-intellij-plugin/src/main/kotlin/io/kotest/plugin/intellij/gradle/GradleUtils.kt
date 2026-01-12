package io.kotest.plugin.intellij.gradle

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import io.kotest.plugin.intellij.run.gradle.GradleTaskNamesBuilder
import org.jetbrains.plugins.gradle.execution.GradleRunnerUtil
import org.jetbrains.plugins.gradle.execution.build.CachedModuleDataFinder
import org.jetbrains.plugins.gradle.service.project.GradleTasksIndices
import org.jetbrains.plugins.gradle.settings.GradleProjectSettings
import org.jetbrains.plugins.gradle.settings.TestRunner
import org.jetbrains.plugins.gradle.util.GradleModuleData
import org.jetbrains.plugins.gradle.util.GradleTaskData

internal object GradleUtils {

   private val LOG = logger<GradleUtils>()

   /**
    * Returns true if we have the Kotest Gradle plugin configured for the given module.
    */
   @Suppress("UnstableApiUsage")
   fun hasKotestGradlePlugin(module: Module?): Boolean {
      if (module == null) return false
      // if we have any Kotest Gradle task in the project, we assume the plugin is applied
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

   @Deprecated("Support in 6.1 has moved to use the standard gradle test tasks")
   fun isKotestTaskName(taskName: String): Boolean {
      return taskName == "kotest" // jvm only task name
         || taskName == "jvmKotest" // multiplatform jvm task name
         || taskName.matches("kotest[a-zA-Z]+UnitTest".toRegex()) // android task names eg kotestReleaseUnitTest, kotestDebugUnitTest, etc.
   }

   @Deprecated("Support in 6.1 has moved to use --tests over a gradle property")
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

   /**
    * Returns the version of Kotest defined for this module or null if Kotest is a dependency.
    * Assumes that all artifacts in the io.kotest group are using the same version.
    */
   fun getKotestVersion(module: Module?): Version? {
      if (module == null) return null
      LOG.info("Getting kotest version for module $module")

      val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(module.project)
      val dependency = libraryTable.libraries.find { it.name?.contains("io.kotest:kotest-framework-engine") ?: false }
      LOG.info("Kotest dependency found $dependency")

      val version = dependency?.name?.substringAfterLast(":") ?: return null
      return VersionParser.parse(version)
   }

   fun isKotest61OrAbove(module: Module?): Boolean {
      if (module == null) return false
      val version = getKotestVersion(module) ?: return false
      return (version.major == 6 && version.minor > 0) || version.major > 6
   }

   fun isGradleTestRunner(module: Module?): Boolean {
      if (module == null) return false
      return GradleProjectSettings.getTestRunner(
         module.project,
         ExternalSystemApiUtil.getExternalProjectPath(module)
      ) != TestRunner.PLATFORM
   }

   fun isPlatformRunner(module: Module?): Boolean {
      if (module == null) return false
      return GradleProjectSettings.getTestRunner(
         module.project,
         ExternalSystemApiUtil.getExternalProjectPath(module)
      ) == TestRunner.PLATFORM
   }
}

