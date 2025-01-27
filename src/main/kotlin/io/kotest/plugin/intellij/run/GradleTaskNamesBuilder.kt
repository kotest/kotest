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
   private val candidates: List<KtClassOrObject>,
   private val test: Test?,
) {

   companion object {

      private const val CANDIDATE_DELIMITER = ";"

      fun builder(gradleModuleData: GradleModuleData): GradleTaskNamesBuilder =
         GradleTaskNamesBuilder(gradleModuleData, emptyList(), null)
   }

   fun withCandidate(candidate: KtClassOrObject): GradleTaskNamesBuilder {
      return copy(candidates = candidates + candidate)
   }

   fun withTest(test: Test?): GradleTaskNamesBuilder {
      return copy(test = test)
   }

   fun build(): List<String> {
      return listOfNotNull(taskArg(), candidatesArg(), descriptorArg())
   }

   private fun taskArg() = gradleModuleData.getTaskPath(Constants.KOTEST_GRADLE_TASK_PREFIX)

   private fun candidatesArg(): String {
      val fqns = candidates.mapNotNull { it.fqName }.joinToString(CANDIDATE_DELIMITER) { it.asString() }
      return "--candidates '$fqns'"
   }

   private fun descriptorArg(): String? {
      if (test == null) return null
      return "--descriptor '${test.descriptor()}'"
   }
}
