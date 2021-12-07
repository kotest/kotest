package com.sksamuel.kotest.specs.funspec

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

// test we allow suspend functions in before / after DSL methods
class FunSpecWithContextBlockTest : FunSpec({
   val messages = mutableListOf<String>()

   afterSpec {
      messages shouldContainExactly mutableListOf(
         "top level, no context, no context",
         "top level, with config defaults",
         "nested level, with context without config",
         "nested level, with context with config defaults",
         "nested level, with context with config enable true"
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
   context("A context will disable tests using test.config") {
      test("Will be enabled") {
         messages.add("nested level, with context without config")
      }
      test("Will be disabled, but shouldn't").config {
         messages.add("nested level, with context with config defaults")
      }
      test("Will be disabled also, but shouldn't").config(enabled = true) {
         messages.add("nested level, with context with config enable true")
      }
      test("Will be disabled but might be coincidental").config(enabled = false) {
         messages.add("nested level, with context with config enable false")
      }
   }
})
