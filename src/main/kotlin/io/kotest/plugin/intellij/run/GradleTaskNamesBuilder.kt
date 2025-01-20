package io.kotest.plugin.intellij.run

import io.kotest.plugin.intellij.Constants
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.plugins.gradle.util.GradleModuleData

/**
 * Builds the gradle command line to execute kotest.
 */
@Suppress("UnstableApiUsage")
data class GradleTaskNamesBuilder(
   private val gradleModuleData: GradleModuleData,
   private val specs: List<KtClass>,
) {

   companion object {

      private const val SPEC_FQN_DELIMITER = ";"

      fun builder(gradleModuleData: GradleModuleData): GradleTaskNamesBuilder =
         GradleTaskNamesBuilder(gradleModuleData, emptyList())
   }

   fun withSpec(spec: KtClass): GradleTaskNamesBuilder {
      return copy(specs = specs + spec)
   }

   fun build(): List<String> {
      val taskName = gradleModuleData.getTaskPath(Constants.GRADLE_TASK_NAME)
      val specFQNs = specs.mapNotNull { it.fqName }.joinToString(SPEC_FQN_DELIMITER) { it.asString() }
      return listOf(
         taskName,
         "--specs '$specFQNs'"
      )
   }
}
