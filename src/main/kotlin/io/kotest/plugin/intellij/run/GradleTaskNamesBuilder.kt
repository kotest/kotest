package io.kotest.plugin.intellij.run

import com.intellij.openapi.module.Module
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.gradle.GradleUtils
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Builds the gradle command line to execute a kotest test.
 */
data class GradleTaskNamesBuilder(
   private val module: Module,
   private val specs: List<KtClassOrObject>,
   private val test: Test?,
) {

   companion object {

      const val SPEC_DELIMITER = ";"
      const val ARG_SPECS = "--specs"
      const val ARG_INCLUDE = "-Pkotest.include"

      fun builder(module: Module): GradleTaskNamesBuilder =
         GradleTaskNamesBuilder(module, emptyList(), null)
   }

   fun withSpec(spec: KtClassOrObject): GradleTaskNamesBuilder {
      return copy(specs = specs + spec)
   }

   fun withTest(test: Test?): GradleTaskNamesBuilder {
      return copy(test = test)
   }

   fun build(): List<String> {
      return taskArgs().flatMap { listOfNotNull(it, specsArg(), includeArg()) }
   }

   @Suppress("UnstableApiUsage")
   private fun taskArgs(): List<String> {
      val tasks = GradleUtils.kotestTasks(module)
      // we should definitely have at least one kotest task in the project, otherwise
      // the GradleKotestTaskRunConfigurationProducer should have skipped this
      if (tasks.isEmpty())
         error(
            "Could not find a kotest task in module ${module.name}. Please ensure the Kotest Gradle plugin is applied. " +
               "Available tasks: ${GradleUtils.listTasks(module).map { it.name }}"
         )
      else return tasks.map { it.getFqnTaskName() }
   }

   private fun specsArg(): String {
      val fqns = specs.mapNotNull { it.fqName }.joinToString(SPEC_DELIMITER) { it.asString() }
      return "$ARG_SPECS '$fqns'"
   }

   private fun includeArg(): String? {
      if (test == null) return null
      return "$ARG_INCLUDE '${test.descriptorPath()}'"
   }
}
