package io.kotest.runner.console

import io.kotest.core.filters.TestCaseFilter
import io.kotest.core.filters.TestFilterResult
import io.kotest.core.spec.Spec
import io.kotest.core.test.Description
import kotlin.reflect.KClass

/**
 * Compares test descriptions to a given test path (delimited with --).
 * The comparison ignores test prefixes.
 */
class TestPathTestCaseFilter(
   testPath: String,
   spec: KClass<out Spec>
) : TestCaseFilter {

   private val target = testPath.split(" -- ").fold(Description.spec(spec)) { acc, name -> acc.append(name) }

   override fun filter(description: Description): TestFilterResult {
      return when {
         description == target || target.isAncestorOf(description) || description.isAncestorOf(target) -> TestFilterResult.Include
         else -> TestFilterResult.Exclude
      }
   }
}
