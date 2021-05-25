package com.sksamuel.kotest.specs.shouldspec

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeLessThan
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.milliseconds

class ShouldSpecTest : ShouldSpec() {

   init {
      context("a context") {
         should("a test") {
            1.shouldBeLessThan(2)
         }
         should("a test with config").config(enabled = true, timeout = Duration.milliseconds(12321)) {
             1.shouldBeLessThan(2)
         }
         context("a nested context") {
            should("a test") {
               1.shouldBeLessThan(2)
            }
         }
         should("a test without a parent context") {
            1.shouldBeLessThan(2)
         }
         xshould("an ignored should test") {
            error("boom")
         }
         xshould("ignored test with config").config(timeout = Duration.milliseconds(12343)) {
             error("boom")
         }
      }
      context("a context with delay in child coroutine") {
         launch { delay(1) }
         should("a test") {
            1.shouldBeLessThan(2)
         }
      }
      xcontext("an ignored should context") {
         should("ignored due to the outer context being disabled") {
            error("boom")
         }
      }
   }
}
