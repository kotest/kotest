package io.kotest.plugin.intellij.run.gradle

import io.kotest.plugin.intellij.Test
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Builds the --tests filter used by gradle to run a subset of tests.
 *
 * For data tests, this builder generates a tag filter instead of a test path filter,
 * since data test names are only known at runtime.
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

   /**
    * Returns true if this filter is for a data test that requires tag-based filtering.
    */
   fun isDataTestFilter(): Boolean = test?.isDataTest == true

   /**
    * Returns the tag expression for data test filtering.
    * Format: "kotest.data.{lineNumber}"
    * Returns null if this is not a data test or line number cannot be determined.
    */
   fun dataTestTagExpression(): String? = test?.dataTestTag()

   fun build(): String {
      return buildString {
         append("--tests '")
         if (spec != null) {
            append(spec.fqName!!.asString())
         }
         // For data tests, we don't append the test path since names are runtime-generated.
         // Instead, the caller should use dataTestTagExpression() to filter by tag.
         if (test != null && !test.isDataTest) {
            append(".")
            append(test.path().joinToString(" -- ") { it.name })
         }
         append("'")
      }
   }
}
