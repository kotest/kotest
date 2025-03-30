package io.kotest.core.listeners

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

/**
 * Invoked once per spec class after all spec instances have completed.
 *
 * This listener is only invoked if the spec had at least one enabled test.
 */
interface FinalizeSpecListener : Extension {

   /**
    * Called once per [Spec], after all tests have completed for that spec.
    *
    * If a spec is instantiated multiple times because the isolation mode
    * is set to create multiple instances, then this listener will not be
    * invoked multiple times.
    *
    * The results' parameter contains every [TestCase], along with
    * the result of that test, including tests that were ignored (which
    * will have a TestResult that has TestStatus.Ignored).
    *
    * @param kclass the [Spec] class
    * @param results a map of each test case mapped to its result.
    */
   suspend fun finalizeSpec(
      kclass: KClass<out Spec>,
      results: Map<TestCase, TestResult>,
   ): Unit = Unit
}
