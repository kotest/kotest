package com.sksamuel.kotest.engine.extensions.guarantees

import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe

class BeforeTestExecutionOrderTest : FunSpec() {
   init {

      var order = ""

      beforeTest {
         order += "a"
      }

      extensions(object : BeforeTestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            order += "b"
         }
      })

      register(object : BeforeTestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            order += "c"
         }
      })

      register(
         object : BeforeTestListener {
            override suspend fun beforeTest(testCase: TestCase) {
               order += "d"
            }
         },
         object : BeforeTestListener {
            override suspend fun beforeTest(testCase: TestCase) {
               order += "e"
            }
         }
      )

      register(object : BeforeTestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            order += "f"
         }
      })

      beforeTest {
         order += "g"
      }

      extensions(object : BeforeTestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            order += "h"
         }
      })

      test("beforeTest extensions registered in a spec should be executed in registration order") {
         order shouldBe "abcdefgh"
      }
   }
}
