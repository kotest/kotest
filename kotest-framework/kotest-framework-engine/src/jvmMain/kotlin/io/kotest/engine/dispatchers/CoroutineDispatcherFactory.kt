package io.kotest.engine.dispatchers

import io.kotest.common.ExperimentalKotest
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
@ExperimentalKotest
interface CoroutineDispatcherFactory {

   /**
    * Called to retrieve a [CoroutineDispatcher] for the given [Spec].
    *
    * Can return new dispatchers on every request, or re-use existing dispatchers.
    */
   @ExperimentalKotest
   fun dispatcherFor(spec: KClass<out Spec>): CoroutineDispatcher

   /**
    * Called to retrieve a [CoroutineDispatcher] for the given [TestCase].
    *
    * Can return new dispatchers on every request, or re-use existing dispatchers.
    */
   @ExperimentalKotest
   fun dispatcherFor(testCase: TestCase): CoroutineDispatcher

   /**
    * Called after a spec has completed executing so this factory can perform any resource
    * clean up required.
    */
   @ExperimentalKotest
   fun complete(spec: KClass<out Spec>) {}

   @ExperimentalKotest
   fun complete(testCase: TestCase) {}

   /**
    * Called once all specs are completed so this factory can perform any resource
    * clean up required.
    */
   @ExperimentalKotest
   fun stop() {}
}

