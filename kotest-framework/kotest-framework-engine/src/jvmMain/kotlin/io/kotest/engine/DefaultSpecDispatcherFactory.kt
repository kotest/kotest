package io.kotest.engine

import io.kotest.core.extensions.CoroutineDispatcherFactoryExtension
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.mpp.bestName
import io.kotest.mpp.log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class DefaultSpecDispatcherFactory(private val threads: Int) : CoroutineDispatcherFactoryExtension {

   private val executors = List(threads) { Executors.newSingleThreadExecutor() }
   private val dispatchers = executors.map { it.asCoroutineDispatcher() }

   override fun dispatcherFor(spec: KClass<out Spec>): CoroutineDispatcher {
      return dispatchers[spec.bestName().hashCode() % threads]
   }

   override fun dispatcherFor(testCase: TestCase): CoroutineDispatcher {
      return dispatchers[testCase.spec::class.bestName().hashCode() % threads]
   }

   override fun stop() {
      executors.forEach { it.shutdown() }
      try {
         executors.forEach { it.awaitTermination(1, TimeUnit.MINUTES) }
      } catch (e: InterruptedException) {
         log("DefaultSpecDispatcherFactory: Interrupted while waiting for dispatcher to terminate", e)
         throw e
      }
   }
}
