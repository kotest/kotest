package io.kotest.plugin.intellij.run

import io.kotest.plugin.intellij.Constants
import io.kotest.plugin.intellij.Test
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.plugins.gradle.util.GradleModuleData

/**
 * Builds the gradle command line to execute kotest.
 */
@Suppress("UnstableApiUsage")
data class GradleTaskNamesBuilder(
   private val gradleModuleData: GradleModuleData,
   private val specs: List<KtClassOrObject>,
   private val test: Test?,
) {

   companion object {
      private const val SPEC_PACKAGE_DELIMITER = ";"
      fun builder(gradleModuleData: GradleModuleData): GradleTaskNamesBuilder =
         GradleTaskNamesBuilder(gradleModuleData, emptyList(), null)
   }

   fun withSpec(spec: KtClassOrObject): GradleTaskNamesBuilder {
      return copy(specs = specs + spec)
   }

   fun withTest(test: Test): GradleTaskNamesBuilder {
      return copy(test = test)
   }

   fun build(): List<String> {
      return listOfNotNull(taskArg(), specsArg(), testArg())
   }

   private fun taskArg() = gradleModuleData.getTaskPath(Constants.GRADLE_TASK_NAME)

   private fun specsArg(): String {
      val specFQNs = specs.mapNotNull { it.fqName }.joinToString(SPEC_PACKAGE_DELIMITER) { it.asString() }
      return "--specs '$specFQNs'"
   }

   private fun testArg(): String? {
      if (test == null) return null
      return "--test '${test.name.name}'"
   }
}
