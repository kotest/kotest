package io.kotest.core.listeners

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

@Deprecated("This has been renamed to FinishSpecListener. This tyepalias is deprecated since 5.0 and will be removed in 6.0")
typealias FinalizeSpecListener = FinishSpecListener

/**
 * Invoked after all tests have completed for a spec.
 * This listener is only invoked if the spec had at least one enabled test.
 */
interface FinishSpecListener : Listener {

   /**
    * Called once per [Spec], after all tests have completed for that spec.
    *
    * Regardless of how many times the spec is instantiated,
    * for example, if [InstancePerTest] or [InstancePerLeaf] isolation
    * modes are used, this callback will only be invoked once.
    *
    * The results' parameter contains every [TestCase], along with
    * the result of that test, including tests that were ignored (which
    * will have a TestResult that has TestStatus.Ignored).
    *
    * @param kclass the [Spec] class
    * @param results a map of each test case mapped to its result.
    */
   @Deprecated(
      "Renamed to finishSpec. Deprecated since 5.0 and will be removed in 6.0",
      ReplaceWith("finishSpec(kclass, results)")
   )
   suspend fun finalizeSpec(
      kclass: KClass<out Spec>,
      results: Map<TestCase, TestResult>,
   ): Unit = Unit

   suspend fun finishSpec(
      kclass: KClass<out Spec>,
      results: Map<TestCase, TestResult>,
   ): Unit = finalizeSpec(kclass, results)
}
