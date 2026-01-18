package io.kotest.plugin.intellij.run.gradle

import io.kotest.plugin.intellij.Test
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Builds the --tests filter used by gradle to run a subset of tests.
 */
data class GradleTestFilterBuilder(
   private val spec: KtClassOrObject?,
   private val test: Test?
) {

   companion object {
      fun builder(): GradleTestFilterBuilder = GradleTestFilterBuilder(null, null)
   }

   fun withSpec(spec: KtClassOrObject): GradleTestFilterBuilder {
      return copy(spec = spec)
   }

   fun withTest(test: Test?): GradleTestFilterBuilder {
      return copy(test = test)
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
          */
         if (test != null && !test.isDataTest) {
            append(".")
            append(test.path().joinToString(" -- ") { it.name })
         }
         append("'")
      }
   }
}
