package com.sksamuel.kotest

import io.kotest.shouldBe
import io.kotest.specs.WordSpec
import io.kotest.whenReady
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.thread

class WhenReadyTest : WordSpec() {
  init {

    "Futures" should {
      "support CompletableFuture<T>" {

        val completableFuture = CompletableFuture<String>()
        thread {
          Thread.sleep(6000)
          completableFuture.complete("wibble")
        }

        whenReady(completableFuture) {
          it shouldBe "wibble"
        }
      }
      "support nested threads" {
        val completableFuture1 = CompletableFuture<String>()
        val completableFuture2 = CompletableFuture<String>()
        val completableFuture3 = CompletableFuture<String>()

        thread {
          Thread.sleep(2000)
          completableFuture1.complete("wibble")
          thread {
            Thread.sleep(2000)
            completableFuture2.complete("wobble")
            thread {
              Thread.sleep(2000)
              completableFuture3.complete("wubble")
            }
          }
        }

        whenReady(completableFuture1) {
          it shouldBe "wibble"
        }

        whenReady(completableFuture2) {
          it shouldBe "wobble"
        }

        whenReady(completableFuture3) {
          it shouldBe "wubble"
        }
      }
    }
  }
}