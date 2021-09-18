package io.kotest.engine.launcher

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.Descriptor.Companion.TestDelimiter
import io.kotest.core.descriptors.append
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.spec.Spec
import io.kotest.core.descriptors.toDescriptor
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

   private val target1 = testPath.split(TestDelimiter)
      .fold(spec.toDescriptor() as Descriptor) { desc, name ->
         desc.append(name)
      }

   // this is a hack where we append "should" to the first name, until 5.0 where we will
   // store names with affixes separately (right now word spec is adding them to the names at source)
   var should = true
   private val target2 = testPath.split(TestDelimiter)
      .fold(spec.toDescriptor() as Descriptor) { desc, name ->
         if (should) {
            should = false
            desc.append("$name should")
         } else desc.append(name)
      }

   override fun filter(descriptor: Descriptor): TestFilterResult {
      return when {
         target1.isOnPath(descriptor) ||
            target2.isOnPath(descriptor) ||
            descriptor.isOnPath(target1) ||
            descriptor.isOnPath(target2) -> TestFilterResult.Include
         else -> TestFilterResult.Exclude
      }
   }
}
