package io.kotest.engine

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.reflect.KClass
import io.kotest.engine.extensions.CoroutineDispatcherExtension

/**
 * To use a custom [CoroutineDispatcherProvider] implement the
 * [CoroutineDispatcherExtension] extension point.
 */
@ExperimentalKotest
interface CoroutineDispatcherProvider {

   /**
    * Called to retrieve a [CoroutineDispatcher] to execute the given spec.
    *
    * The returned dispatcher will then be used with a `withContext` call to
    * switch execution to a new coroutine using that dispatcher which will then
    * be used when executing that spec.
    *
    * Once the spec is completed and execution returns back to the original coroutine,
    * [release] is invoked with the spec in question.
    *
    * If the implementation does not wish to change the dispatcher used, then return null.
    *
    * If this implementation wishes to track dispatchers and which specs they have
    * been allocated to, then they should keep internal state.
    */
   fun acquire(spec: KClass<*>): CoroutineDispatcher?

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
    * If the implementation does not wish to change the dispatcher used, then return null.
    *
    * If this implementation wishes to track dispatchers and which tests they have
    * been allocated to, then they should keep internal state.
    *
    * @param testCase the [TestCase] that will be executed on the supplied dispatcher.
    */
   fun acquire(testCase: TestCase): CoroutineDispatcher?

   /**
    * Invoked when the test coroutine has completed.
    *
    * @param testCase the [TestCase] that was completed.
    */
   fun release(testCase: TestCase)

   /**
    * Invoked when the spec has completed.
    *
    * @param spec the kclass of the spec that was completed.
    */
   fun release(spec: KClass<out Spec>)
}

/**
 * An implementation of [CoroutineDispatcherProvider] that does not switch dispatchers.
 */
object NoopCoroutineDispatcherProvider : CoroutineDispatcherProvider {
   override fun acquire(spec: KClass<*>): CoroutineDispatcher? = null
   override fun acquire(testCase: TestCase): CoroutineDispatcher? = null
   override fun release(testCase: TestCase) {}
   override fun release(spec: KClass<out Spec>) {}
}

expect val defaultCoroutineDispatcherProvider: CoroutineDispatcherProvider
