package io.kotest.core.listeners

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

interface SpecIgnoredListener : Listener {

   /**
    * Called once per [Spec] iff all tests in the spec are inactive.
    *
    * @param spec the [Spec] instance.
    * @param results a map of each test case mapped to its skipped result.
    */
   suspend fun specIgnored(spec: Spec, results: Map<TestCase, TestResult>): Unit = Unit
}

