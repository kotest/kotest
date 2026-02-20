package com.sksamuel.kotest.wasm

import io.kotest.core.spec.style.FunSpec

class BangTest : FunSpec({

   test("!This test is skipped because of the bang") {
      error("This test starts with a bang, so it should not execute")
   }

   test("This test should run") {
      check(true)
   }

   context("!This suite is skipped") {
      test("This test shouldn't run") {
         error("This test should not execute due to the bang in the suite name")
      }
   }

   context("This suite is not skipped") {
      test("This test is not skipped") {
         check(true)
      }

      test("!This test is skipped") {
         error("This test should not execute due to the bang")
      }
   }
})
