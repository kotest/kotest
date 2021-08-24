package io.kotest.engine.concurrency

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.reflect.KClass

/**
 * To use a custom [CoroutineDispatcherProvider] implement the
 * [CoroutineDispatcherExtension] extension point.
 */
@ExperimentalKotest
interface CoroutineDispatcherProvider {

   /**
    * Called to retrieve a [CoroutineDispatcher] for the given [Spec].
    */
   fun acquire(spec: KClass<*>): CoroutineDispatcher

   /**
    * Called to retrieve a [CoroutineDispatcher] for the given [TestCase].
    *
    * The returned dispatcher will then be used with a `withContext` call to
    * switch execution to a new coroutine using this dispatcher which then will be
    * used to invoke the test.
    *
    * Once the test is completed and execution returns back to the original coroutine,
    * [release] is invoked with the test case in question.
    *
    * If this implementation wishes to track dispatchers and which tests they have
    * been allocated to, then they should keep internal state.
    *
    * @param testCase the [TestCase] that will be executed on the supplied dispatcher.
    */
   fun acquire(testCase: TestCase): CoroutineDispatcher

   /**
    * Invoked when the test coroutine has completed.
    *
    * @param testCase the [TestCase] that was completed.
    */
   fun release(testCase: TestCase)

   fun release(spec: KClass<out Spec>)
}
