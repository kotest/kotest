package com.sksamuel.kotest.matchers.future

import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.future.shouldBeCancelled
import io.kotest.matchers.future.shouldBeCompleted
import io.kotest.matchers.future.shouldBeCompletedExceptionally
import io.kotest.matchers.future.shouldCompleteExceptionallyWith
import io.kotest.matchers.future.shouldNotBeCancelled
import io.kotest.matchers.future.shouldNotBeCompleted
import io.kotest.matchers.future.shouldNotBeCompletedExceptionally
import io.kotest.matchers.future.shouldNotCompleteExceptionallyWith
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

@OptIn(ExperimentalCoroutinesApi::class)
class FutureMatcherTest : StringSpec({

   suspend fun runOnSeparateThread(block: () -> Unit) {
      @OptIn(DelicateCoroutinesApi::class)
      newSingleThreadContext("separate").use {
         withContext(it) {
            block()
         }
      }
   }

   "test future is completed" {
      val completableFuture = CompletableFuture<Int>()
      completableFuture.complete(2)
      completableFuture.shouldBeCompleted()
   }
   "test future is not completed" {
      val completableFuture = CompletableFuture<Int>()
      completableFuture.shouldNotBeCompleted()
   }
   "test future is cancelled" {
      val completableFuture = CompletableFuture<Int>()
      completableFuture.cancel(true)
      completableFuture.shouldBeCancelled()
   }
   "test future is not cancelled" {
      val completableFuture = CompletableFuture<Int>()
      completableFuture.shouldNotBeCancelled()
   }
   "test future is completed exceptionally" {
      val completableFuture = CompletableFuture<Int>()
      runOnSeparateThread {
         completableFuture.cancel(false)
      }
      delay(200)
      completableFuture.shouldBeCompletedExceptionally()
   }
   "test future is not completed exceptionally" {
      val completableFuture = CompletableFuture<Int>()
      completableFuture.complete(2)
      completableFuture.shouldNotBeCompletedExceptionally()
   }
   "test future completes exceptionally with the given exception" {
      val completableFuture = CompletableFuture<Int>()
      val exception = RuntimeException("Boom Boom")

      runOnSeparateThread {
         completableFuture.completeExceptionally(exception)
      }

      completableFuture shouldCompleteExceptionallyWith exception
   }
   "test future does not completes exceptionally with given exception " {
      val completableFuture = CompletableFuture<Int>()

      runOnSeparateThread {
         completableFuture.completeExceptionally(RuntimeException("Boom Boom"))
      }

      completableFuture shouldNotCompleteExceptionallyWith RuntimeException("Bang Bang")
   }
   "test error message for shouldCompleteExceptionallyWith when completable future does not complete with exception" {
      val completableFuture = CompletableFuture<Int>()
      val exception = RuntimeException("Boom Boom")

      completableFuture.complete(2)

      shouldThrowMessage("Expected future to fail with $exception, but it did not failed with any exception") {
         completableFuture shouldCompleteExceptionallyWith exception
      }
   }
   "test error message for shouldCompleteExceptionallyWith when completable future fail with some other exception" {
      val completableFuture = CompletableFuture<Int>()
      val expectedException = RuntimeException("Boom Boom")
      val actualException = RuntimeException("Bang Bang")
      completableFuture.completeExceptionally(actualException)

      shouldThrowMessage("Expected future to fail with $expectedException, but it failed with $actualException") {
         completableFuture shouldCompleteExceptionallyWith expectedException
      }
   }
   "test error message for shouldNotCompleteExceptionallyWith when completable future completes with given exception" {
      val completableFuture = CompletableFuture<Int>()
      val exception = RuntimeException("Boom Boom")
      completableFuture.completeExceptionally(exception)

      shouldThrowMessage("Expected future not to fail with $exception, but it did fail with it.") {
         completableFuture shouldNotCompleteExceptionallyWith exception
      }
   }
   "test shouldNotCompleteExceptionallyWith passes when completable future completes without any exception" {
      val completableFuture = CompletableFuture<Int>()
      completableFuture.complete(2)

      completableFuture shouldNotCompleteExceptionallyWith RuntimeException("Bang Bang")
   }
   "test error message for shouldNotBeCompletedExceptionally when completable future completes with exception" {
      val completableFuture = CompletableFuture<Int>()
      val exception = RuntimeException("Bruh", RuntimeException("Another bruh"))
      completableFuture.completeExceptionally(exception)

      shouldThrowMessage("Future should not be completed exceptionally, but it failed with $exception") {
         completableFuture.shouldNotBeCompletedExceptionally()
      }
   }
})
