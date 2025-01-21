package io.kotest.plugin.intellij.run

import io.kotest.plugin.intellij.Constants
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.plugins.gradle.util.GradleModuleData

/**
 * Builds the gradle command line to execute kotest.
 */
@Suppress("UnstableApiUsage")
data class GradleTaskNamesBuilder(
   private val gradleModuleData: GradleModuleData,
   private val specs: List<KtClassOrObject>,
) {

   companion object {

      private const val SPEC_FQN_DELIMITER = ";"

      fun builder(gradleModuleData: GradleModuleData): GradleTaskNamesBuilder =
         GradleTaskNamesBuilder(gradleModuleData, emptyList())
   }

   fun withSpec(spec: KtClassOrObject): GradleTaskNamesBuilder {
      return copy(specs = specs + spec)
   }

   fun build(): List<String> {
      return listOf(taskArg(), specsArg())
   }

   private fun taskArg() = gradleModuleData.getTaskPath(Constants.GRADLE_TASK_NAME)

   private fun specsArg(): String {
      val specFQNs = specs.mapNotNull { it.fqName }.joinToString(SPEC_FQN_DELIMITER) { it.asString() }
      return "--specs '$specFQNs'"
   }
}
