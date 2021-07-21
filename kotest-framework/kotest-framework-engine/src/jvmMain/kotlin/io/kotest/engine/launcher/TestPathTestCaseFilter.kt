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

   private val target1 = testPath.split(TestPathSeparator)
      .fold(spec.toDescription() as Description) { desc, name -> desc.appendTest(name) }

   // this is a hack where we append "should" to the first name, until 5.0 where we will
   // store names with affixes separately (right now word spec is adding them to the names at source)
   var should = true
   private val target2 = testPath.split(TestPathSeparator)
      .fold(spec.toDescription() as Description) { desc, name ->
         if (should) {
            should = false
            desc.appendTest("$name should")
         } else desc.appendTest(name)
      }

   override fun filter(description: Description): TestFilterResult {
      return when {
         target1.isOnPath(description) ||
            target2.isOnPath(description) ||
            description.isOnPath(target1) ||
            description.isOnPath(target2) -> TestFilterResult.Include
         else -> TestFilterResult.Exclude
      }
   }
}
