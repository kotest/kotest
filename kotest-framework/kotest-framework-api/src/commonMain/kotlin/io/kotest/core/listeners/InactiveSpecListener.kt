package io.kotest.core.listeners

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Invoked if a [Spec] has no enabled root tests.
 *
 * For example, this spec has no active tests:
 *
 * ```
 * class MySpec : FunSpec({
 *   test("!disabled") { }
 * })
 *
 * Therefore, any registered [InactiveSpecListener]s would be notified, along with a
 * results map that contains the test cases that were disabled.
 *
 * For a similar listener that is fired when a spec is skipped completely, without
 * even being instantiated, then see [IgnoredSpecListener].
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

