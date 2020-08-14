package io.kotest.core.spec

import io.kotest.core.test.TestCase

interface Spec : FunctionConfiguration, FunctionCallbacks, InlineCallbacks, InlineConfiguration {

   /**
    * Returns the root tests of this spec.
    */
   fun rootTests(): List<RootTest>

   /**
    * Materializes the tests defined in this spec as [TestCase] instances.
    */
   fun materializeRootTests(): List<TestCase>
}

data class RootTest(val testCase: TestCase, val order: Int)
