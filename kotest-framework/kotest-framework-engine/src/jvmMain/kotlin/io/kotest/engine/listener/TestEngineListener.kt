package io.kotest.engine.listener

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

/**
 * Implementations of this interface will be notified of events
 * that occur as part of the [KotestEngine] lifecycle.
 *
 * This is an internal listener liable to be changed.
 */
interface TestEngineListener {

   /**
    * Is invoked when the [KotestEngine] is starting execution.
    *
    * @param classes the [Spec] classes that will be used by the [KotestEngine].
    */
   fun engineStarted(classes: List<KClass<out Spec>>) {}

   /**
    * Is invoked when the [KotestEngine] has finished execution.
    *
    * If an unrecoverable error was detected during execution then it will be passed
    * as the parameter to the engine.
    */
   fun engineFinished(t: List<Throwable>) {}

   /**
    * Is invoked once per [Spec] to indicate that this spec is about to
    * begin execution.
    */
   fun specStarted(kclass: KClass<out Spec>) {}

   /**
    * Is invoked once per [Spec] to indicate that all [TestCase] instances
    * of the spec have completed.
    *
    * @param kclass the spec that has completed
    * @param t if not null, then an error that occured when trying to execute this spec
    * @param results if t is null, then the results of the tests that were submitted.
    */
   fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>) {}

   /**
    * Invoked if a [TestCase] is about to be executed (is active).
    * Will not be invoked if the test is ignored.
    */
   fun testStarted(testCase: TestCase) {}

   /**
    * Invoked if a [TestCase] will not be executed because it is ignored (not active).
    */
   fun testIgnored(testCase: TestCase, reason: String?) {}

   /**
    * Invoked when all the invocations of a [TestCase] have completed.
    * This function will only be invoked if a test case was active.
    * The result passed in here is the result directly from the test run, before any interception.
    */
   fun testFinished(testCase: TestCase, result: TestResult) {}

   /**
    * Invoked each time an instance of a [Spec] is created.
    * A spec may be created once per class, or one per [TestCase].
    */
   fun specInstantiated(spec: Spec) {}

   fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) {}

}
