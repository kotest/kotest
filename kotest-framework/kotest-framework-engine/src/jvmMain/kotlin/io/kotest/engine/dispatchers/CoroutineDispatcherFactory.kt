package io.kotest.engine.dispatchers

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.reflect.KClass

/**
 * Allocates [CoroutineDispatcher]s to [Spec]s and [TestCase]s.
 *
 * To use a custom [CoroutineDispatcherFactory] implement the [CoroutineDispatcherFactoryExtension]
 * extension point.
 */
interface CoroutineDispatcherFactory {

   /**
    * Called to retrieve a [CoroutineDispatcher] for the given [Spec].
    *
    * Can return new dispatchers on every request, or re-use existing dispatchers.
    */
   fun dispatcherFor(spec: KClass<out Spec>): CoroutineDispatcher

   /**
    * Called to retrieve a [CoroutineDispatcher] for the given [TestCase].
    *
    * Can return new dispatchers on every request, or re-use existing dispatchers.
    */
   fun dispatcherFor(testCase: TestCase): CoroutineDispatcher

   /**
    * Called after a spec has completed executing so this factory can perform any resource
    * clean up required.
    */
   fun complete(spec: KClass<out Spec>) {}

   /**
    * Called once all specs are completed so this factory can perform any resource
    * clean up required.
    */
   fun stop() {}
}

