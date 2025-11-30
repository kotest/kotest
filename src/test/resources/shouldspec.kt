package com.sksamuel.kotest.specs.shouldspec

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData

class ShouldSpecExample : ShouldSpec() {
   init {
      should("top level test") {
         // test here
      }
      should("top level test with config").config(enabled = true) {
         // test here
      }
      context("some context") {
         should("top level test") {
            // test here
         }
         should("top level test with config").config(enabled = true) {
            // test here
         }
      }
      context("some context 2") {
         context("some nested context") {
            should("top level test") {
               // test here
            }
            should("top level test with config").config(enabled = true) {
               // test here
            }
         }
      }
      context("a context with config").config(enabled = true) {
         should("a should") {}
      }
      xcontext("an xcontext with config").config(enabled = true) {
         should("a should") {}
      }
      xshould("xdisabled should"){

      }

      withData(1, 2, 3, 4, 5) { value ->
         // test here
      }
   }
}
