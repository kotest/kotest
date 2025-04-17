package io.kotest.engine.listener

import io.kotest.common.KotestInternal
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.TestEngine
import io.kotest.engine.interceptors.EngineContext
import kotlin.reflect.KClass

/**
 * Implementations of this interface will be notified of events
 * that occur as part of the [TestEngine] lifecycle.
 *
 * This is public but should be considered internal.
 */
@KotestInternal
interface TestEngineListener {

   /**
    * Invoked as soon as the engine has been created.
    */
   suspend fun engineStarted()

   /**
    * Invoked when the [TestEngine] has completed setup and is ready to begin
    * executing specs.
    *
    * @param context the final context that will be used.
    */
   suspend fun engineInitialized(context: EngineContext)

   /**
    * Is invoked when the [TestEngine] has finished execution of all tests.
    *
    * If any unexpected errors were detected during execution then they will be
    * passed to this method.
    */
   suspend fun engineFinished(t: List<Throwable>)

   /**
    * Invoked once per [Spec] to indicate that this spec will be instantiated
    * and any active tests invoked.
    */
   suspend fun specStarted(kclass: KClass<*>)

   /**
    * Invoked when a spec is ignored. An optional [reason] for being ignored can be provided.
    */
   suspend fun specIgnored(kclass: KClass<*>, reason: String?)

   /**
    * Is invoked once per [Spec] class to indicate this spec has completed.
    */
   suspend fun specFinished(kclass: KClass<*>, result: TestResult)

   /**
    * Invoked if a [TestCase] is about to be executed.
    * Will not be invoked if the test is ignored.
    */
   suspend fun testStarted(testCase: TestCase)

   /**
    * Invoked if a [TestCase] will be skipped.
    */
   suspend fun testIgnored(testCase: TestCase, reason: String?)

   /**
    * Invoked when all the invocations of a [TestCase] have completed.
    * This function will only be invoked if a test case was enabled.
    */
   suspend fun testFinished(testCase: TestCase, result: TestResult)
}

/**
 * Implementation of [TestEngineListener] that provides no-op implementations for each method.
 * This is useful for testing when you only want to override a single method.
 */
@KotestInternal
abstract class AbstractTestEngineListener : TestEngineListener {
   override suspend fun engineStarted() {}
   override suspend fun engineFinished(t: List<Throwable>) {}
   override suspend fun engineInitialized(context: EngineContext) {}
   override suspend fun specStarted(kclass: KClass<*>) {}
   override suspend fun specFinished(kclass: KClass<*>, result: TestResult) {}
   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) {}
   override suspend fun testFinished(testCase: TestCase, result: TestResult) {}
   override suspend fun testIgnored(testCase: TestCase, reason: String?) {}
   override suspend fun testStarted(testCase: TestCase) {}
}

@KotestInternal
val NoopTestEngineListener = object : AbstractTestEngineListener() {}
