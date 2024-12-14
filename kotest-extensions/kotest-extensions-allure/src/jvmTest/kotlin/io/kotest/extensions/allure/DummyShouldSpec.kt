package io.kotest.extensions.allure

import io.kotest.core.spec.Order
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.booleans.shouldBeTrue

@Order(0)
class DummyShouldSpec : ShouldSpec() {
   init {
      context("a") {
         should("work") {
            true.shouldBeTrue()
         }
      }
      context("b") {
         should("work") {
            true.shouldBeTrue()
         }
      }
   }
}
