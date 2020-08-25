package io.kotest.core.listeners

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import kotlin.reflect.KClass

interface TestListener : Listener {

   override val name: String
      get() = "defaultTestListener"

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

   /**
    * Registers a new before-container callback to be executed before every [TestCase]
    * with type [TestType.Container].
    * The [TestCase] about to be executed is provided as the parameter.
    */
   suspend fun beforeContainer(testCase: TestCase): Unit = Unit

   /**
    * Registers a new after-container callback to be executed after every [TestCase]
    * with type [TestType.Container].
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   suspend fun afterContainer(testCase: TestCase, result: TestResult): Unit = Unit

   /**
    * Registers a new before-each callback to be executed before every [TestCase]
    * with type [TestType.Test].
    * The [TestCase] about to be executed is provided as the parameter.
    */
   suspend fun beforeEach(testCase: TestCase): Unit = Unit

   /**
    * Registers a new after-each callback to be executed after every [TestCase]
    * with type [TestType.Test].
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   suspend fun afterEach(testCase: TestCase, result: TestResult): Unit = Unit

   /**
    * Registers a new before-any callback to be executed before every [TestCase]
    * with type [TestType.Test] or [TestType.Container].
    * The [TestCase] about to be executed is provided as the parameter.
    */
   suspend fun beforeAny(testCase: TestCase): Unit = Unit

   /**
    * Registers a new after-container callback to be executed after every [TestCase]
    * with type [TestType.Container] or [TestType.Test].
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   suspend fun afterAny(testCase: TestCase, result: TestResult): Unit = Unit

   /**
    * Invoked before each 'run' of a test, with a flag indicating the iteration number.
    * This callback is useful if you have set a test to have multiple invocations via config and want to do
    * some setup / teardown between runs.
    *
    * If you are running a test with the default single invocation then this callback is effectively the
    * same as [beforeTest].
    *
    * Note: If you have set multiple invocations _and_ multiple threads, then these callbacks could be
    * invoked concurrently.
    */
   suspend fun beforeInvocation(testCase: TestCase, iteration: Int): Unit = Unit

   /**
    * Invoked after each 'run' of a test, with a flag indicating the iteration number.
    * This callback is useful if you have set a test to have multiple invocations via config and want to do
    * some setup / teardown between runs.
    *
    * If you are running a test with the default single invocation then this callback is effectively the
    * same as [afterTest].
    *
    * Note: If you have set multiple invocations _and_ multiple threads, then these callbacks could be
    * invoked concurrently.
    */
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
    * modes are used, this callback will only be invoked once.
    *
    * @param kclass the [Spec] class
    */
   suspend fun prepareSpec(kclass: KClass<out Spec>): Unit = Unit

   /**
    * Called once per [Spec], after all tests have completed for that spec.
    *
    * Regardless of how many times the spec is instantiated,
    * for example, if [InstancePerTest] or [InstancePerLeaf] isolation
    * modes are used, this callback will only be invoked once.
    *
    * The results parameter contains every [TestCase], along with
    * the result of that test, including tests that were ignored (which
    * will have a TestResult that has TestStatus.Ignored).
    *
    * @param kclass the [Spec] class
    * @param results a map of each test case mapped to its result.
    */
   suspend fun finalizeSpec(
      kclass: KClass<out Spec>,
      results: Map<TestCase, TestResult>
   ): Unit = Unit
}
