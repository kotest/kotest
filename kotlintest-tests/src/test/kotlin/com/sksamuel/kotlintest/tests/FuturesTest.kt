package com.sksamuel.kotlintest.tests

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.whenReady
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.thread

class FuturesTest : WordSpec() {
  init {

    val completableFuture = CompletableFuture<String>()
    thread {
      Thread.sleep(6000)
      completableFuture.complete("wibble")
    }

    "Futures" should {
      "support CompletableFuture<T>" {
        whenReady(completableFuture) {
          it shouldBe "wibble"
        }
      }
    }
  }
}