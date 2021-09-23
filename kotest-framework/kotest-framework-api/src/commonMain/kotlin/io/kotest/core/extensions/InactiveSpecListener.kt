package io.kotest.core.extensions

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Invoked if a [Spec] has no enabled tests.
 */
interface InactiveSpecListener : Extension {

   /**
    * Called once per [Spec] iff all tests in the spec are disabled.
    *
    * @param spec the [Spec] instance.
    * @param results a map of each test case mapped to its skipped result.
    */
   suspend fun inactive(spec: Spec, results: Map<TestCase, TestResult>) {}
}

