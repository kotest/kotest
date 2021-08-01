package io.kotest.engine.extensions

import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.TestEngineListener

/**
 * Extension that intercepts calls to the engine.
 *
 * This extension can be used to execute code before or after the engine, change
 * the [TestEngineListener], or adapt the [TestSuite].
 *
 * Extensions of this type are not designed to be used by third party addons.
 * We reserve the right to change the implementation of these extensions with regards
 * to breaking changes.
 */
interface EngineExtension {
   fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult
}
