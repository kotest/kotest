package io.kotest.engine.launcher

import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.spec.Spec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.Description
import io.kotest.core.test.TestPathSeparator
import kotlin.reflect.KClass

/**
 * Compares test descriptions to a given test path (delimited with ' -- ').
 * The comparison ignores test prefixes, so an application using the launcher should not
 * include test name prefixes in the test path.
 */
class TestPathTestCaseFilter(
   testPath: String,
   spec: KClass<out Spec>,
) : TestFilter {

   private val target = testPath.split(TestPathSeparator)
      .fold(spec.toDescription() as Description) { desc, name -> desc.appendTest(name) }

   override fun filter(description: Description): TestFilterResult {
      return when {
         target.isOnPath(description) || description.isOnPath(target) -> TestFilterResult.Include
         else -> TestFilterResult.Exclude
      }
   }
}
