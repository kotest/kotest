package com.sksamuel.kotest.engine.extensions.guarantees

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
class BeforeTestExecutionOrderTest : FunSpec() {

   var order = ""

   override suspend fun beforeTest(testCase: TestCase) {
      order += "!!"
   }

   init {


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

      beforeTest {
         order += "d"
      }

      register(
         object : BeforeTestListener {
            override suspend fun beforeTest(testCase: TestCase) {
               order += "e"
            }
         },
         object : BeforeTestListener {
            override suspend fun beforeTest(testCase: TestCase) {
               order += "f"
            }
         }
      )

      register(object : BeforeTestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            order += "g"
         }
      })

      beforeTest {
         order += "h"
      }

      extensions(object : BeforeTestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            order += "i"
         }
      })

      register(object : BeforeTestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            order += "j"
         }
      })

      test("beforeTest extensions registered in a spec should be executed in registration order") {
         order shouldBe "!!abcdefghij"
      }
   }
}
