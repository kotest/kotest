package io.kotest.engine.launcher

import io.kotest.common.TestPath
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.filter.TestFilterResult

/**
 * A  [TestSelector] is a marker interface is used to select tests and/or specs to execute.
 */
interface TestSelector {
   /**
    * This method is invoked with a [Descriptor] and the result
    * used to determine if the test should be included or not.
    */
   fun filter(descriptor: Descriptor): TestFilterResult
}

/**
 * A [PackageTestSelector] is used to select all tests from a package.
 */
class PackageTestSelector(private val pkg: String) : TestSelector {
   override fun filter(descriptor: Descriptor): TestFilterResult {
      TODO("Not yet implemented")
   }
}

sealed interface TestSelectorResult {
   data object Include : TestSelectorResult
   data class Exclude(val reason: String?) : TestSelectorResult
}

/**
 * A [TestPathTestSelector] is used to select all tests that begin with the given path.
 *
 * Note that paths should never include spec style affixes. Those are for display only and
 * not used during test resolution.
 */
class TestPathTestSelector(private val path: TestPath) : TestSelector {
   override fun filter(descriptor: Descriptor): TestFilterResult {
      TODO("Not yet implemented")
   }
}
