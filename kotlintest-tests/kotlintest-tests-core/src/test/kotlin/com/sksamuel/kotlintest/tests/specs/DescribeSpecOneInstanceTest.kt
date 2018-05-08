package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import java.util.concurrent.atomic.AtomicInteger

class DescribeSpecOneInstanceTest : DescribeSpec() {

  override fun isInstancePerTest(): Boolean = true

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