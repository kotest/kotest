package io.kotest.plugin.intellij.run.gradle

import io.kotest.plugin.intellij.Test
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Builds the --tests filter used by gradle to run a subset of tests.
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

   fun build(): String {
      return buildString {
         append("--tests '")
         if (spec != null) {
            append(spec.fqName!!.asString())
         }
         /**
          * For data tests, we don't append the test path since names are runtime-generated.
          * Instead, we use tag based filtering to select data tests, handled via
          * [GradleMultiplatformJvmTestTaskRunProducer.setOrRemoveDataTestEnvVarIfNeeded] and its callers.
          *
          * However, if the data test is inside a regular context, we append the ancestor path
          * so that only tests within that specific context are run.
          */
         if (test != null && !test.isDataTest) {
            append(".")
            append(test.path().joinToString(" -- ") { it.name })
         } else if (dataTestAncestorPath != null) {
            // Data test inside a regular context - include the ancestor path
            append(".")
            append(dataTestAncestorPath)
         }
         append("'")
      }
   }
}
