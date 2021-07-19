package io.kotest.engine.launcher

import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.plan.Descriptor
import io.kotest.core.plan.TestPath
import io.kotest.core.spec.Spec
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * Compares test descriptions to a given test path (delimited with ' -- ').
 * The comparison ignores test affixes, so an application using the launcher should not
 * include test name affixes in the input test path.
 *
 * @param testPath should not include test affixes
 */
class TestPathTestFilter(
   testPath: String,
   spec: KClass<out Spec>,
) : TestFilter {

   private val target: TestPath = TestPath(testPath).prepend(spec.bestName())

   // this is a hack where we append "should" to the first name, until 5.0 where we will
   // store names with affixes separately (right now word spec is adding them to the names at source)
   // todo fix hack
   //   var should = true

   override fun filter(descriptor: Descriptor.TestDescriptor): TestFilterResult {
      // given a test path of "context -- context2" and a descriptor for "context -- context2 -- test"
      // we want to include that test but not if we have "context -- context2 -- test2"
      descriptor.testPath(true)
      return when {
         descriptor.testPath(true) == target -> TestFilterResult.Include
         descriptor.testPath(true).value.startsWith(target.value) -> TestFilterResult.Include
         target.value.startsWith(descriptor.testPath(true).value) -> TestFilterResult.Include
         else -> TestFilterResult.Exclude
      }
   }
}
