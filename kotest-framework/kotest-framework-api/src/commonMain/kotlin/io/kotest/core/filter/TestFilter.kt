package io.kotest.core.filter

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.test.TestCase

/**
 * A [TestFilter] can be used to filter tests before they are executed.
 *
 * A descriptor must be included by all filters for it to be considered enabled at runtime.
 */
@Suppress("DEPRECATION") // Remove when removing Filter
interface TestFilter : Filter {

   /**
    * This method is invoked with a [TestCase] and the result
    * used to determine if the test should be included or not.
    */
   fun filter(descriptor: Descriptor): TestFilterResult
}

sealed interface TestFilterResult {
   object Include : TestFilterResult {
      override fun toString() = "TestFilterResult.Include" // Replace me with `data object` when it's available
   }
   data class Exclude(val reason: String?) : TestFilterResult
}

fun Boolean.toTestFilterResult(reason: String?) =
   if (this) TestFilterResult.Include else TestFilterResult.Exclude(reason)
