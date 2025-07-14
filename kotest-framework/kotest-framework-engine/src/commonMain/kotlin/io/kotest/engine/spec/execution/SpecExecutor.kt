package io.kotest.engine.spec.execution

import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult

internal abstract class SpecExecutor {

   /**
    * Executes the given [io.kotest.core.spec.SpecRef] using the provided [seed] spec as the initial state.
    * Returns a map of [io.kotest.core.test.TestCase] to [TestResult] for all tests that were executed to be
    * passed back to the SpecRef interceptor pipeline.
    */
   abstract suspend fun execute(ref: SpecRef, seed: Spec): Result<Map<TestCase, TestResult>>
}
