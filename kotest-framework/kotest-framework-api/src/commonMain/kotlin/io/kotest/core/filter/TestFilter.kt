package io.kotest.core.filter

import io.kotest.core.test.Description
import io.kotest.core.test.TestCase

/**
 * A [TestFilter] can be used to filter [Description]s before they are executed.
 * These filters are passed to the Kotest Engine at runtime.
 *
 * A description must be included by all filters for it to be executed at runtime.
 */
interface TestFilter : Filter {

   /**
    * This method is invoked with a [TestCase] and the result
    * used to determine if the test should be included or not.
    */
   fun filter(description: Description): TestFilterResult
}

enum class TestFilterResult {
   Include, Exclude
}

fun Boolean.toTestFilterResult() = if (this) TestFilterResult.Include else TestFilterResult.Exclude
