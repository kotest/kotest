package io.kotest.engine.listener

import io.kotest.common.ExperimentalKotest
import io.kotest.core.plan.Descriptor
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.TestEngine
import kotlin.reflect.KClass

/**
 * Implementations of this interface will be notified of events
 * that occur as part of the [TestEngine] lifecycle.
 *
 * This is an internal interface liable to be changed without notice.
 */
interface TestEngineListener {

   /**
    * Is invoked as soon as the engine has been created and before any other interceptors run.
    * // todo add config as arg here.
    */
   suspend fun engineInitialize() {}

   /**
    * Is invoked when the [TestEngine] is starting execution.
    *
    * @param classes the [Spec] classes that will be used by the [TestEngine].
    */
   suspend fun engineStarted(classes: List<KClass<*>>) {}

   /**
    * Is invoked when the [TestEngine] has finished execution.
    *
    * If an unrecoverable error was detected during execution then it will be passed
    * as the parameter to the engine.
    */
   suspend fun engineFinished(t: List<Throwable>) {}

   /**
    * Is invoked when the engine has finished with all tests and other interceptors.
    * Is the last thing in the engine before the engine terminates.
    */
   suspend fun engineFinalize() {}

   /**
    * Is invoked once per [Spec] to indicate that this spec is ready to begin
    * executing tests.
    *
    * Note: This function differs from [prepareSpec] in that it will
    * only be executed if the spec is active and has enabled tests.
    */
   suspend fun specStarted(kclass: KClass<*>) {}

   /**
    * Is invoked once per [Spec] class to indicate this spec has been submitted
    * to the spec executor. This callback is invoked before any other interceptors
    * are invoked, and thus will always be called, even if the spec is later skipped.
    */
   suspend fun prepareSpec(kclass: KClass<*>) {}

   /**
    * Is invoked once per [Spec] to indicate that this spec is ready to begin
    * executing tests.
    *
    * Note: This function differs from [prepareSpec] in that it will
    * only be executed if the spec is active and has enabled tests.
    */
   suspend fun specStarted(descriptor: Descriptor.SpecDescriptor) {}

   /**
    * Is invoked once per [Spec] to indicate that all [TestCase] instances
    * of the spec have completed.
    *
    * Note: This function differs from [finalizeSpec] in that it will
    * only be executed if the spec was active and had enabled tests.
    *
    * @param kclass the spec that has completed
    * @param t if not null, then an error that occured when trying to execute this spec
    * @param results if t is null, then the results of the tests that were submitted.
    */
   suspend fun specFinished(kclass: KClass<*>, t: Throwable?, results: Map<TestCase, TestResult>) {}

   /**
    * Is invoked once per [Spec] class to indicate this spec has finished all other operations
    * in the spec executor. This callback is invoked after any other interceptors
    * are invoked, and thus will always be called, even if the spec has been skipped.
    */
   suspend fun finalizeSpec(kclass: KClass<*>) {}

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

   /**
    * Invoked when a spec is ignored without being instantiated or executed.
    */
   fun specIgnored(kclass: KClass<out Spec>) {}
}

val NoopTestEngineListener = object : TestEngineListener {}
