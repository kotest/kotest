package io.kotest.core.listeners

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Invoked if a [Spec] has no enabled root tests.
 *
 * For example,
 *
 * ```
 * class MySpec : FunSpec({
 *   test("!disabled") { }
 * })
 *
 * Has no active tests. Therefore, this listener would be notified, along with a
 * results map that contains the test cases and a TestResult of ignored.
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

