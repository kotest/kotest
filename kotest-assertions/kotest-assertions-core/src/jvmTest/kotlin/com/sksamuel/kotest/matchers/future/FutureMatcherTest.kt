package com.sksamuel.kotest.matchers.future

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
})
