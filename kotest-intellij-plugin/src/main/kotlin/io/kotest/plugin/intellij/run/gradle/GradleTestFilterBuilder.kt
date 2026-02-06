package io.kotest.plugin.intellij.run.gradle

import io.kotest.plugin.intellij.Test
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Builds the --tests filter used by Gradle to run a subset of tests.
 */
data class GradleTestFilterBuilder(
   private val spec: KtClassOrObject?,
   private val test: Test?,
   private val dataTestAncestorPath: String? = null
) {

   companion object {
      fun builder(): GradleTestFilterBuilder = GradleTestFilterBuilder(null, null, null)
   }

   fun withSpec(spec: KtClassOrObject): GradleTestFilterBuilder {
      return copy(spec = spec)
   }

   fun withTest(test: Test?): GradleTestFilterBuilder {
      return copy(test = test)
   }

   /**
    * Sets the ancestor test path for data tests that are inside regular contexts.
    * This ensures the filter includes the parent context name so only tests within that context run.
    */
   fun withDataTestAncestorPath(path: String?): GradleTestFilterBuilder {
      return copy(dataTestAncestorPath = path)
   }

   fun build(includeTestsFlag: Boolean): String {
      return buildString {
         if (includeTestsFlag)
            append("--tests ")
         append("'")
         if (spec != null) {
            append(spec.fqName!!.asString())
         }
         appendTestPath()
         append("'")
      }
   }

   /**
    * Appends the test path to the filter based on the test type:
    *
    * - **Regular test**: Append the full test path (e.g., `MySpec.context -- test name`)
    * - **Data test inside a regular context**: Append only the ancestor path to scope the run
    *   to that context, while tag-based filtering selects the specific data test
    * - **Root-level data test**: No path appended; tag-based filtering handles selection
    *
    * see [GradleMultiplatformJvmTestTaskRunProducer.setOrRemoveDataTestEnvVarIfNeeded] for context on data test handling.
    */
   private fun StringBuilder.appendTestPath() {
      when {
         // Regular test - use full path
         test != null && !test.isDataTest -> test.path().joinToString(" -- ") { it.name }
         // Data test inside a regular context - use ancestor path to scope the run
         dataTestAncestorPath != null -> dataTestAncestorPath
         // Root-level data test or no test - no path needed
         else -> null
      }?.let {
         append(".")
         append(it)
      }
   }
}
