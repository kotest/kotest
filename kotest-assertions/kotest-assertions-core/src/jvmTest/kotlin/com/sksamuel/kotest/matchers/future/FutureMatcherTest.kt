package com.sksamuel.kotest.matchers.future

import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.future.*
import kotlinx.coroutines.delay
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class FutureMatcherTest : StringSpec({
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
      Executors.newFixedThreadPool(1).submit {
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
   "test future completes exceptionally with the given exception"{
      val completableFuture = CompletableFuture<Int>()
      val exception = RuntimeException("Boom Boom")

      Executors.newFixedThreadPool(1).submit {
         completableFuture.completeExceptionally(exception)
      }

      completableFuture shouldCompleteExceptionallyWith exception
   }
   "test future does not completes exceptionally with given exception " {
      val completableFuture = CompletableFuture<Int>()

      Executors.newFixedThreadPool(1).submit {
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
})
