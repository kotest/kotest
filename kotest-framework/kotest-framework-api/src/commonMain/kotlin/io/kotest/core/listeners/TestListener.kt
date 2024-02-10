package io.kotest.core.listeners

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

@Deprecated("Renamed to IgnoredSpecListener. Deprecated since 5.0")
typealias SpecIgnoredListner = IgnoredSpecListener
// 'Listner' is intentionally misspelled to match the original from 4.6
// https://github.com/kotest/kotest/blob/2853bf6e2e7d7c136fd2748da100b75e5d050c29/kotest-framework/kotest-framework-api/src/commonMain/kotlin/io/kotest/core/listeners/spec.kt#L49


/**
 * A [TestListener] contains functions that are invoked as part of the lifecycle of a [TestCase].
 *
 * This interface is a union of the various test related listeners interfaces.
 * Users can choose to extend this interface, or the constituent interfaces separately.
 */
interface TestListener :
   BeforeListener,
   AfterListener,
   BeforeTestListener,
   AfterTestListener,
   BeforeContainerListener,
   AfterContainerListener,
   BeforeEachListener,
   AfterEachListener,
   BeforeSpecListener,
   AfterSpecListener,
   BeforeInvocationListener,
   AfterInvocationListener,
   PrepareSpecListener,
   FinalizeSpecListener,
   Listener {

   override val name: String
      get() = "defaultTestListener"
}
