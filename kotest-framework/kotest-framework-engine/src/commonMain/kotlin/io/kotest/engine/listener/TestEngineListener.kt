package io.kotest.engine.listener

import io.kotest.common.ExperimentalKotest
import io.kotest.core.plan.Descriptor
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

/**
 * Implementations of this interface will be notified of events
 * that occur as part of the [KotestEngine] lifecycle.
 *
 * This is an internal interface liable to be changed without notice.
 */
interface TestEngineListener {

   /**
    * Is invoked when the [KotestEngine] is starting execution.
    *
    * @param classes the [Spec] classes that will be used by the [KotestEngine].
    */
   suspend fun engineStarted(classes: List<KClass<*>>) {}

   /**
    * Is invoked when the [KotestEngine] has finished execution.
    *
    * If an unrecoverable error was detected during execution then it will be passed
    * as the parameter to the engine.
    */
   suspend fun engineFinished(t: List<Throwable>) {}

   /**
    * Is invoked once per [Spec] to indicate that this spec is about to
    * begin execution.
    */
   suspend fun specStarted(kclass: KClass<*>) {}

   /**
    * Is invoked once per [Spec] to indicate that this spec is about to
    * begin execution.
    */
   suspend fun specStarted(descriptor: Descriptor.SpecDescriptor) {}

   /**
    * Is invoked once per [Spec] to indicate that all [TestCase] instances
    * of the spec have completed.
    *
    * @param kclass the spec that has completed
    * @param t if not null, then an error that occured when trying to execute this spec
    * @param results if t is null, then the results of the tests that were submitted.
    */
   suspend fun specFinished(kclass: KClass<*>, t: Throwable?, results: Map<TestCase, TestResult>) {}

   @ExperimentalKotest
   suspend fun specFinished(
      descriptor: Descriptor.SpecDescriptor,
      t: Throwable?,
      results: Map<Descriptor.TestDescriptor, TestResult>
   ) {
   }

   /**
    * Invoked if a [TestCase] is about to be executed.
    * Will not be invoked if the test is ignored.
    */
   suspend fun testStarted(testCase: TestCase) {}

   /**
    * Invoked if a [TestCase] is about to be executed.
    * Will not be invoked if the test is ignored.
    */
   @ExperimentalKotest
   suspend fun testStarted(descriptor: Descriptor.TestDescriptor) {
   }

   /**
    * Invoked if a [TestCase] will not be executed because it is not enabled.
    */
   suspend fun testIgnored(testCase: TestCase, reason: String?) {}

   @ExperimentalKotest
   suspend fun testIgnored(descriptor: Descriptor.TestDescriptor, reason: String?) {
   }

   /**
    * Invoked when all the invocations of a [TestCase] have completed.
    * This function will only be invoked if a test case was active.
    * The result passed in here is the result directly from the test run, before any interception.
    */
   suspend fun testFinished(testCase: TestCase, result: TestResult) {}

   @ExperimentalKotest
   suspend fun testFinished(descriptor: Descriptor.TestDescriptor, result: TestResult) {
   }

   /**
    * Invoked each time an instance of a [Spec] is created.
    * A spec may be created once per class, or one per [TestCase].
    */
   suspend fun specInstantiated(spec: Spec) {}

   suspend fun specInstantiationError(kclass: KClass<*>, t: Throwable) {}

}

val NoopTestEngineListener = object : TestEngineListener {}
