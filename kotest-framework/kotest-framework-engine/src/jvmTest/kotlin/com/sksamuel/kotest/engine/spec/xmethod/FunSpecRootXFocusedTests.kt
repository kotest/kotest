package com.sksamuel.kotest.engine.spec.xmethod

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds

class FunSpecRootXFocusedTests : FunSpec() {
   init {

      var tests = 0

      afterSpec {
         tests shouldBe 4
      }

      context("root context without config") {
         error("boom") // will be ignored because of a focused test
      }

      context("root context with config").config(timeout = 10.seconds) {
         error("boom") // will be ignored because of a focused test
      }

      fcontext("focused root context without config") {
         tests++
      }

      fcontext("focused root context with config").config(timeout = 10.seconds) {
         tests++
      }

      xcontext("disabled root context without config") {
         error("boom") // will be ignored because of xmethod
      }

      xcontext("disabled root context with config").config(timeout = 10.seconds) {
         error("boom") // will be ignored because of xmethod
      }

      test("root test without config") {
         error("boom") // will be ignored because of a focused test
      }

      test("root test with config").config(timeout = 10.seconds) {
         error("boom") // will be ignored because of a focused test
      }

      ftest("focused root test without config") {
         tests++
      }

      ftest("focused root test with config").config(timeout = 10.seconds) {
         tests++
      }

      xtest("disabled root test without config") {
         error("boom") // will be ignored because of xmethod
      }

      xtest("disabled root test with config").config(timeout = 10.seconds) {
         error("boom") // will be ignored because of xmethod
      }
   }
}
