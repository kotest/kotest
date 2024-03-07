package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder

// Verifies fix for : https://github.com/kotest/kotest/issues/2710
class FunSpecConfigEnabledTest : FunSpec({
   val messages = mutableListOf<String>()

   afterSpec {
      messages shouldContainExactlyInAnyOrder mutableListOf(
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
      messages.add("top level, with config enabled false")
   }
   test("Will be correctly enabled").config(enabledIf = {true}) {
      messages.add("top level, with config enabledIf giving true")
   }
   test("Will be correctly disabled, for enabled if").config(enabledIf = {false}) {
      messages.add("top level, with config enabledIf giving false")
   }
   xtest("Will be disable by xtest, config have enabled set").config(enabled = true, enabledIf = {true}) {
      messages.add("xtest should not be called")
   }
   xtest("Will be disable by xtest, no config") {
      messages.add("xtest without config should not be called")
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
         messages.add("nested level, with context with config enable false")
      }
      xtest("Will be disable by xtest, config have enabled set").config(enabled = true, enabledIf = {true}) {
         messages.add("xtest, inside context, should not be called")
      }
      xtest("Will be disable by xtest, no config") {
         messages.add("xtest, inside context, without config should not be called")
      }
   }
})
