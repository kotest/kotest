package com.sksamuel.kotest.specs.describe

import io.kotest.IsolationMode
import io.kotest.shouldBe
import io.kotest.specs.DescribeSpec
import java.util.concurrent.atomic.AtomicInteger

class DescribeSpecOneInstanceTest : DescribeSpec() {

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

  val count = AtomicInteger(0)

  init {
    describe("1") {
      count.incrementAndGet().shouldBe(1)
      it("1.1") {
        count.incrementAndGet().shouldBe(2)
      }
      context("1.2") {
        count.incrementAndGet().shouldBe(2)
        it("1.2.1") {
          count.incrementAndGet().shouldBe(3)
        }
        context("1.2.2") {
          count.incrementAndGet().shouldBe(3)
          it("1.2.2.1") {
            count.incrementAndGet().shouldBe(4)
          }
          it("1.2.2.2") {
            count.incrementAndGet().shouldBe(4)
          }
        }
      }
    }
    describe("2") {
      count.incrementAndGet().shouldBe(1)
      context("2.1") {
        count.incrementAndGet().shouldBe(2)
        it("2.1.1") {
          count.incrementAndGet().shouldBe(3)
        }
        it("2.1.2") {
          count.incrementAndGet().shouldBe(3)
        }
      }
    }
  }
}
