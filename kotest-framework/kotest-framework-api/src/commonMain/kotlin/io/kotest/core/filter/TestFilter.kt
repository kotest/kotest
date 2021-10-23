package io.kotest.core.filter

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.test.TestCase

/**
 * A [TestFilter] can be used to filter tests before they are executed.
 *
 * A descriptor must be included by all filters for it to be considered enabled at runtime.
 */
interface TestFilter : Filter {

   /**
    * This method is invoked with a [TestCase] and the result
    * used to determine if the test should be included or not.
    */
   fun filter(descriptor: Descriptor): TestFilterResult
}

enum class TestFilterResult {
   Include, Exclude
}

fun Boolean.toTestFilterResult() = if (this) TestFilterResult.Include else TestFilterResult.Exclude
