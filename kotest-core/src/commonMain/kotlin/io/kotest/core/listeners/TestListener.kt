package io.kotest.core.listeners

import io.kotest.core.spec.IsolationMode.InstancePerLeaf
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

interface TestListener : Listener {

   /**
    * This callback will be invoked before a [TestCase] is executed.
    *
    * If a test case is inactive (disabled), then this method will not
    * be invoked for that particular test case.
    *
    * @param testCase the [TestCase] about to be executed.
    */
   suspend fun beforeTest(testCase: TestCase): Unit = Unit

   /**
    * This callback is invoked after a [TestCase] has finished.
    *
    * If a test case was skipped (ignored / disabled / inactive) then
    * this callback will not be invoked for that particular test case.
    *
    * @param testCase the [TestCase] that has completed.
    */
   suspend fun afterTest(testCase: TestCase, result: TestResult): Unit = Unit

   suspend fun beforeInvocation(testCase: TestCase, iteration: Int): Unit = Unit

   suspend fun afterInvocation(testCase: TestCase, iteration: Int): Unit = Unit

   /**
    * This callback is invoked after the Engine instantiates a [Spec]
    * to be used as part of a [TestCase] execution.
    *
    * If a spec is instantiated multiple times - for example, if
    * [InstancePerTest] or [InstancePerLeaf] isolation
    * modes are used, then this callback will be invoked for each instance
    * created, just before the first test (or only test) is executed for that spec.
    *
    * This callback should be used if you need to perform setup
    * each time a new spec instance is created. If you simply need to
    * perform setup once per class file, then use [prepareSpec].
    *
    * @param spec the [Spec] instance.
    */
   suspend fun beforeSpec(spec: Spec): Unit = Unit

   /**
    * Is invoked after the [TestCase]s that are part of a particular
    * [Spec] instance have completed.
    *
    * If a spec is instantiated multiple times - for example, if
    * [InstancePerTest] or [InstancePerLeaf] isolation modes are used,
    * then this callback will be invoked for each instantiated spec,
    * after the tests that are applicable to that spec instance have
    * returned.
    *
    * This callback should be used if you need to perform cleanup
    * after each individual spec instance. If you simply need to
    * perform cleanup once per class file, then use [finalizeSpec].
    *
    * @param spec the [Spec] instance.
    */
   suspend fun afterSpec(spec: Spec): Unit = Unit

   /**
    * Called once per [Spec], when the engine is preparing to
    * execute the tests for that spec.
    *
    * Regardless of how many times the spec is instantiated,
    * for example, if [InstancePerTest] or [InstancePerLeaf] isolation
    * modes are used, this callback will only be invoked once,
    * with the first instance of the Spec.
    *
    * The top level tests declared in the spec are supplied as a list of
    * instances of [RootTest] which includes a flag set to true if
    * the test is active or false if inactive.
    *
    * If there are no active tests in a spec, then this callback will
    * still be invoked.
    *
    * The order of the list of tests is the same as the
    * order of execution.
    *
    * @param kclass the [Spec] class
    * @param tests the list of top level tests
    */
   suspend fun prepareSpec(kclass: KClass<out Spec>): Unit = Unit

   /**
    * Called once per [Spec], after all tests have completed for that spec.
    *
    * Regardless of how many times the spec is instantiated,
    * for example, if [InstancePerTest] or [InstancePerLeaf] isolation
    * modes are used, this callback will only be invoked once,
    * with the first instance of the Spec.
    *
    * The results parameter contains every [TestCase], along with
    * the result of that test, including tests that were ignored (which
    * will have a TestResult that has TestStatus.Ignored)
    *
    * @param spec the [Spec] instance
    * @param results a map of each test case mapped to its result.
    */
   suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>): Unit = Unit
}

data class RootTest(val testCase: TestCase, val order: Int)
