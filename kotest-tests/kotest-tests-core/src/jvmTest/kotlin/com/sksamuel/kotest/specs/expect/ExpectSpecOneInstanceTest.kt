package com.sksamuel.kotest.specs.expect

import io.kotest.IsolationMode
import io.kotest.shouldBe
import io.kotest.specs.ExpectSpec
import java.util.concurrent.atomic.AtomicInteger

class ExpectSpecOneInstanceTest : ExpectSpec() {

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

  val count = AtomicInteger(0)

  init {
    context("1") {
      count.incrementAndGet().shouldBe(1)
      expect("1.1") {
        count.incrementAndGet().shouldBe(2)
      }
      context("1.2") {
        count.incrementAndGet().shouldBe(2)
        expect("1.2.1") {
          count.incrementAndGet().shouldBe(3)
        }
        context("1.2.2") {
          count.incrementAndGet().shouldBe(3)
          expect("1.2.2.1") {
            count.incrementAndGet().shouldBe(4)
          }
          expect("1.2.2.2") {
            count.incrementAndGet().shouldBe(4)
          }
        }
      }
    }
    context("2") {
      count.incrementAndGet().shouldBe(1)
      context("2.1") {
        count.incrementAndGet().shouldBe(2)
        expect("2.1.1") {
          count.incrementAndGet().shouldBe(3)
        }
        expect("2.1.2") {
          count.incrementAndGet().shouldBe(3)
        }
      }
    }
  }
}
