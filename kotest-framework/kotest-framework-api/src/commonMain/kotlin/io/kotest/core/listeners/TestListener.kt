package io.kotest.core.listeners

import io.kotest.core.test.TestCase

/**
 * A [TestListener] contains functions that are invoked as part of the lifecycle of a [TestCase].
 * Brings together the various test-case related listeners. Exists for historical reasons.
 * Users can choose to extend this, or the constituent interfaces seperately.
 */
interface TestListener :
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
