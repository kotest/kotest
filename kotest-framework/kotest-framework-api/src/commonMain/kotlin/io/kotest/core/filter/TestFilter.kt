package io.kotest.core.filter

import io.kotest.core.plan.Descriptor
import io.kotest.core.test.TestCase

/**
 * A [TestFilter] can be used to filter [Descriptor.TestDescriptor]s before they are executed.
 * These filters are passed to the Kotest Engine at runtime.
 *
 * A description must be included by all filters for it to be executed at runtime.
 */
interface TestFilter : Filter {

   /**
    * This method is invoked with a [TestCase] and the result
    * used to determine if the test should be included or not.
    */
   fun filter(descriptor: Descriptor.TestDescriptor): TestFilterResult
}

enum class TestFilterResult {
   Include, Exclude
}

fun Boolean.toTestFilterResult() = if (this) TestFilterResult.Include else TestFilterResult.Exclude
