package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.annotation.Description
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder

// Verifies fix for : https://github.com/kotest/kotest/issues/2710
@Description("tests the various ways config can enable/disable tests")
class FunSpecConfigEnabledTest : FunSpec({
   val messages = mutableListOf<String>()

   afterSpec {
      messages shouldContainExactlyInAnyOrder listOf(
         "top level, no context, no context",
         "top level, with config defaults",
         "nested level, with context without config",
         "nested level, with context with config defaults",
         "nested level, with context with config enable true",
         "top level, with config enabledIf giving true"
      )
   }

   test("Will be enabled") {
      messages.add("top level, no context, no context")
   }
   test("Will be enabled also").config {
      messages.add("top level, with config defaults")
   }
   test("Will be correctly disabled").config(enabled = false) {
      error("boom")
   }
   test("Will be correctly enabled").config(enabledIf = { true }) {
      messages.add("top level, with config enabledIf giving true")
   }
   test("Will be correctly disabled, for enabled if").config(enabledIf = { false }) {
      error("boom")
   }
   xtest("xtest will override any enabled flags").config(enabled = true, enabledIf = { true }) {
      error("boom")
   }
   xtest("Will be disable by xtest, no config") {
      error("boom")
   }
   context("A context will disable tests using test.config") {
      test("Will be enabled") {
         messages.add("nested level, with context without config")
      }
      test("Will be disabled, with empty config").config {
         messages.add("nested level, with context with config defaults")
      }
      test("Will be disabled also, with enabled true").config(enabled = true) {
         messages.add("nested level, with context with config enable true")
      }
      test("Will be disabled, with enabled false").config(enabled = false) {
         error("boom")
      }
      xtest("Will be disable by xtest, config have enabled set").config(enabled = true, enabledIf = { true }) {
         error("boom")
      }
      xtest("Will be disable by xtest, no config") {
         error("boom")
      }
   }
})
