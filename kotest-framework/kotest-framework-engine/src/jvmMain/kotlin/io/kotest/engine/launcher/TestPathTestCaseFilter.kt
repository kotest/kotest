package io.kotest.engine.launcher

import io.kotest.common.TestPath
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.append
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.spec.Spec
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.test.path.TestPathBuilder
import kotlin.reflect.KClass

/**
 * Compares test descriptions to a given test path (delimited with ' -- ').
 * The comparison ignores style prefixes, so an application using the launcher should not
 * include style prefixes in the test path.
 */
class TestPathTestCaseFilter(
   private val testPath: TestPath,
   spec: KClass<out Spec>,
) : TestFilter {

   private val target: Descriptor = testPath.value.split(TestPathBuilder.TEST_DELIMITER)
      .fold(spec.toDescriptor() as Descriptor) { desc, name ->
         desc.append(name)
      }

   private fun List<Descriptor>.prefixesWithWildcardMatch(that: List<Descriptor>): Boolean {
      if (this.isEmpty()) {
         return true
      }
      if (this.size > that.size) {
         return false
      }

      val first = this[0]
      val second = that[0]
      if (!first.isEqualType(second)) {
         return false
      }
      return first.id.wildCardMatch(second.id) && this.subList(1, this.size)
         .prefixesWithWildcardMatch(that.subList(1, that.size))
   }

   /**
    * This filter is called in a tree like manner so there are two cases possible
    * first we check if the group we are currently running is parent of the test filter.
    * Returning true at this point means that test engine will keep on going recursively deeper into the
    * hierarchy
    * Eventually we are deep into the hierarchy where we have runnable targets that will be on the
    * path of the filters which means they can be run
    */
   override fun filter(descriptor: Descriptor): TestFilterResult {
      val descriptorPrefix = descriptor.getTreePrefix()
      val targetPrefix = target.getTreePrefix()
      val onTestFilterPath = descriptorPrefix.prefixesWithWildcardMatch(targetPrefix)
      val testOnDescriptorPath = targetPrefix.prefixesWithWildcardMatch(descriptorPrefix)
      return when {
         onTestFilterPath || testOnDescriptorPath -> TestFilterResult.Include
         else -> TestFilterResult.Exclude("Excluded by test path filter: '$testPath'")
      }
   }
}
