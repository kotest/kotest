package io.kotest.engine.listener

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.TestEngine
import kotlin.reflect.KClass

/**
 * Implementations of this interface will be notified of events
 * that occur as part of the [TestEngine] lifecycle.
 */
interface TestEngineListener {

   /**
    * Is invoked as soon as the engine has been created.
    * No other callbacks or lifecycle hooks will execute before this one.
    */
   suspend fun engineStartup() {}

   /**
    * Is invoked when the [TestEngine] is starting execution.
    *
    * This callback is executed after all other interceptors have completed
    * and the engine setup is complete.
    *
    * @param classes the [Spec] classes that will be used by the [TestEngine].
    */
   suspend fun engineStarted(classes: List<KClass<*>>) {}

   /**
    * Is invoked when the [TestEngine] has finished execution of all specs.
    * This callback is invoked before execution returns to the interceptor chain.
    *
    * If an unrecoverable error was detected during execution then it will be passed
    * as the parameter to the engine.
    */
   suspend fun engineFinished(t: List<Throwable>) {}

   /**
    * Is invoked when the engine has finished with all tests and other interceptors.
    * Is the last thing in the engine before the engine terminates.
    */
   suspend fun engineShutdown() {}

   /**
    * Invoked when a spec is submitted to the SpecExecutor.
    * This callback is invoked before any of the spec level interceptors.
    */
   suspend fun specEnter(kclass: KClass<out Spec>) {}

   /**
    * Invoked when a spec is ignored without being instantiated or executed.
    */
   suspend fun specIgnored(kclass: KClass<out Spec>) {}

   /**
    * Invoked when a spec has no active tests.
    *
    * @param results a map of each root test to an ignored status with possible disabled reason.
    */
   suspend fun specInactive(kclass: KClass<*>, results: Map<TestCase, TestResult>) {}

   /**
    * Invoked each time an instance of a [Spec] is created.
    * A spec may be created once per class, or once per [TestCase].
    */
   suspend fun specInstantiated(spec: Spec) {}

   /**
    * Invoked if an instance of a [Spec] fails to be created reflectively.
    */
   suspend fun specInstantiationError(kclass: KClass<*>, t: Throwable) {}

   /**
    * Invoked if the spec cannot be instantiated in order to check for active tests.
    */
   suspend fun specAborted(kclass: KClass<*>, t:Throwable) {}

   /**
    * Is invoked once per [Spec] to indicate that this spec is active and ready
    * to begin executing tests.
    *
    * Note: This function differs from [specEnter] in that it will
    * only be executed if the spec is active and has enabled tests.
    */
   suspend fun specStarted(kclass: KClass<*>) {}

   /**
    * Is invoked once per [Spec] to indicate that all [TestCase] instances
    * of the spec have completed.
    *
    * Note: This function differs from [specExit] in that it will
    * only be executed if the spec was active.
    *
    * @param kclass the spec that has completed
    * @param results if t is null, then the results of the tests that were submitted.
    */
   suspend fun specFinished(kclass: KClass<*>, results: Map<TestCase, TestResult>) {}

   /**
    * Is invoked once per [Spec] class to indicate this spec has finished all other operations
    * in the spec executor. This callback is invoked after any other interceptors
    * are invoked, and thus will always be called, even if the spec has been skipped.
    */
   suspend fun specExit(kclass: KClass<out Spec>, t: Throwable?) {}

   /**
    * Invoked if a [TestCase] is about to be executed.
    * Will not be invoked if the test is ignored.
    */
   suspend fun testStarted(testCase: TestCase) {}

   /**
    * Invoked if a [TestCase] will not be executed because it is not enabled.
    */
   suspend fun testIgnored(testCase: TestCase, reason: String?) {}

   /**
    * Invoked when all the invocations of a [TestCase] have completed.
    * This function will only be invoked if a test case was active.
    * The result passed in here is the result directly from the test run, before any interception.
    */
   suspend fun testFinished(testCase: TestCase, result: TestResult) {}
}

val NoopTestEngineListener = object : TestEngineListener {}
