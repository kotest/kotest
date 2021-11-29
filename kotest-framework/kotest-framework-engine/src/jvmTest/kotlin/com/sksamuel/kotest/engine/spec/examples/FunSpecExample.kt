package com.sksamuel.kotest.engine.spec.examples

import io.kotest.core.spec.style.FunSpec
import kotlin.time.Duration.Companion.milliseconds

class FunSpecExample : FunSpec() {
   init {

      context("this is a context") {

         context("this is a nested context") {

            test("this is a nested nested test") {
            }

         }

         test("this is a nested test") {
         }

         test("this is a nested test with config").config(timeout = 712.milliseconds, enabled = true) {
         }
      }

      context("this is an xcontext") {

         test("this is a nested test") {
         }

      }

      context("this is a context with config").config(timeout = 611.milliseconds) {

         test("this is a nested test") {
         }

         test("this is a nested test 2") {
         }

      }

      xcontext("this is an xcontext with config").config(timeout = 176.milliseconds) {

         test("this is a nested test") {
         }

      }

      test("this is a test") {
      }

      xtest("this is an xtest") {
      }

      test("this test has config").config(timeout = 412.milliseconds, enabled = true) {
      }

      test("this xtest has config").config(timeout = 911.milliseconds) {
      }
   }
}
