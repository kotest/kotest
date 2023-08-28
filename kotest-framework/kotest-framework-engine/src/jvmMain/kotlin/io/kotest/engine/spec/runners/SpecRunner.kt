package io.kotest.engine.spec.runners

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Interface for executing all tests inside a given [Spec].
 *
 * Each implementation of a [SpecRunner] determines how to instantiate fresh instances
 * as required for the isolation mode.
 */
internal interface SpecRunner {

   /**
    * Executes all the tests in this spec, returning a Failure if there was an exception in a listener
    * or class initializer. Otherwise, returns the results for the tests in that spec.
    */
   suspend fun execute(spec: Spec): Result<Map<TestCase, TestResult>>
}
