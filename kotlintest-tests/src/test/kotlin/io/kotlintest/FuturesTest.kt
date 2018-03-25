package io.kotlintest

import io.kotlintest.runner.junit5.specs.WordSpec
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