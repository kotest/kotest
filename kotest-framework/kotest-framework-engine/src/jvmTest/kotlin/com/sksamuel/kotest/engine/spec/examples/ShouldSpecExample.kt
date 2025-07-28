package com.sksamuel.kotest.engine.spec.examples

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.should
import io.kotest.matchers.string.haveLength

class ShouldSpecExample : ShouldSpec() {
   init {
      should("top level test") {
         // test here
      }
      should("top level test with config").config(enabled = true) {
         // test here
      }
      context("a context") {
         should("do a test") {
            // test here
         }
         should("have config").config(enabled = true) {
            // can use should here
            "string" should haveLength(6)
         }
      }
      context("another context") {
         context("a nested context") {
            should("do a test") {
               // test here
            }
         }
      }
   }
}
