package io.kotest.plugin.intellij.run

import com.intellij.openapi.module.Module
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.gradle.GradleUtils
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Builds the gradle command line to execute a kotest task.
 */
data class GradleTaskNamesBuilder(
   private val module: Module,
   private val specs: List<KtClassOrObject>,
   private val test: Test?,
) {

   companion object {

      const val SPEC_DELIMITER = ";"
      const val SPECS_ARG = "--specs"
      const val DESCRIPTOR_ARG = "--descriptor"

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
      return taskArgs().flatMap { listOfNotNull(it, specsArg(), descriptorArg()) }
   }

   @Suppress("UnstableApiUsage")
   private fun taskArgs(): List<String> {
      // if we have a multiplatform project, we might have jsKotest, jvmKotest, or nativeKotest tasks all registered
      // we'll invoke them all for now, but we should be better about picking one based on the source set?
      // todo use sourceset or some other way of narrowing down the appropriate task
      val tasks = GradleUtils.listTasks(module).filter { it.name.lowercase().endsWith("kotest") }
      if (tasks.isEmpty())
         error("Could not find a kotest task in module ${module.name}. Please ensure the Kotest Gradle plugin is applied.")
      else return tasks.map { it.getFqnTaskName() }
   }

   private fun specsArg(): String {
      val fqns = specs.mapNotNull { it.fqName }.joinToString(SPEC_DELIMITER) { it.asString() }
      return "$SPECS_ARG '$fqns'"
   }

   private fun descriptorArg(): String? {
      if (test == null) return null
      return "$DESCRIPTOR_ARG '${test.descriptor()}'"
   }
}
