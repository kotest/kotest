package io.kotest.engine.spec

import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.mpp.NamedThreadFactory
import io.kotest.engine.instantiateSpec
import io.kotest.fp.Try
import io.kotest.mpp.log
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine
import java.util.concurrent.*
import kotlin.reflect.KClass

/**
 * The base class for executing all the tests inside a [Spec].
 *
 * Each spec can define how tests are isolated from each other, via an [IsolationMode].
 *
 * @param listener provides callbacks on tests as they are executed. These callbacks are used
 * to ultimately feed back into the test engine implementation.
 */
abstract class SpecRunner(val listener: TestEngineListener) {

   /**
    * Executes all the tests in this spec, returning a Failure if there was an exception in a listener
    * or class initializer. Otherwise returns the results for the tests in that spec.
    */
   abstract suspend fun execute(spec: Spec): Try<Map<TestCase, TestResult>>

   /**
    * Creates an instance of the supplied [Spec] by delegating to the project constructors,
    * and notifies the [TestEngineListener] of the instantiation event.
    */
   protected fun createInstance(kclass: KClass<out Spec>): Try<Spec> =
      instantiateSpec(kclass).onSuccess {
         Try { listener.specInstantiated(it) }
      }.onFailure {
         it.printStackTrace()
         Try { listener.specInstantiationError(kclass, it) }
      }

   protected suspend fun runParallel(threads: Int, run: suspend () -> Unit) {
      val executor = Executors.newFixedThreadPool(threads, NamedThreadFactory("SpecRunner-%d"))
      val ctx = ExecutorServiceContext(executor)
      val futures = (0 until threads).map {
         future(ctx) { run() }
      }
      executor.shutdown()
      log("Waiting for test case execution to terminate")

      try {
         executor.awaitTermination(1, TimeUnit.DAYS)
      } catch (t: InterruptedException) {
         log("Test case execution interrupted", t)
         throw t
      }

      //Handle Uncaught Exception in threads or they will just be swallowed
      try {
         futures.forEach { it.get() }
      } catch (e: ExecutionException) {
         throw e.cause ?: e
      }
   }

   protected suspend fun runParallel(threads: Int, testCases: Collection<TestCase>, run: suspend (TestCase) -> Unit) {

      val executor = Executors.newFixedThreadPool(threads, NamedThreadFactory("SpecRunner-%d"))
      val ctx = ExecutorServiceContext(executor)
      val futures = testCases.map { testCase ->
         future(ctx) { run(testCase) }
      }

      executor.shutdown()
      log("Waiting for test case execution to terminate")

      try {
         executor.awaitTermination(1, TimeUnit.DAYS)
      } catch (t: InterruptedException) {
         log("Test case execution interrupted", t)
         throw t
      }

      //Handle Uncaught Exception in threads or they just be swallowed
      try {
         futures.forEach { it.get() }
      } catch (e: ExecutionException) {
         throw e.cause ?: e
      }
   }
}

/**
 * Wraps an [ExecutorService] in a [CoroutineContext] as a [ContinuationInterceptor]
 * scheduling on the [ExecutorService] when [kotlin.coroutines.intrinsics.intercepted] is called.
 */
private class ExecutorServiceContext(val pool: ExecutorService) :
   AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
   override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> =
      ExecutorServiceContinuation(pool, continuation.context.fold(continuation) { cont, element ->
         if (element != this@ExecutorServiceContext && element is ContinuationInterceptor)
            element.interceptContinuation(cont) else cont
      })
}

/** Wrap existing continuation to resumes itself on the provided [ExecutorService] */
private class ExecutorServiceContinuation<T>(val pool: ExecutorService, val cont: Continuation<T>) : Continuation<T> {
   override val context: CoroutineContext = cont.context

   override fun resumeWith(result: Result<T>) {
      pool.execute { cont.resumeWith(result) }
   }
}

private fun <A> future(ctx: CoroutineContext, f: suspend () -> A): Future<A> =
   CompletableFuture<A>().apply {
      f.startCoroutine(Continuation(ctx) { res ->
         res.fold(::complete, ::completeExceptionally)
      })
   }
