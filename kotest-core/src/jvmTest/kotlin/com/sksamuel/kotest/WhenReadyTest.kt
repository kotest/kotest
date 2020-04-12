package com.sksamuel.kotest

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.whenReady
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.thread

class WhenReadyTest : WordSpec() {
   init {

      "Futures" should {
         "support CompletableFuture<T>" {

            val completableFuture = CompletableFuture<String>()
            thread {
               Thread.sleep(1000)
               completableFuture.complete("wibble")
            }

            completableFuture.whenReady {
               it shouldBe "wibble"
            }
         }
         "support nested threads" {
            val completableFuture1 = CompletableFuture<String>()
            val completableFuture2 = CompletableFuture<String>()
            val completableFuture3 = CompletableFuture<String>()

            thread {
               Thread.sleep(500)
               completableFuture1.complete("wibble")
               thread {
                  Thread.sleep(500)
                  completableFuture2.complete("wobble")
                  thread {
                     Thread.sleep(500)
                     completableFuture3.complete("wubble")
                  }
               }
            }

            completableFuture1.whenReady {
               it shouldBe "wibble"
            }

            completableFuture2.whenReady {
               it shouldBe "wobble"
            }

            completableFuture3.whenReady {
               it shouldBe "wubble"
            }
         }
      }
   }
}
