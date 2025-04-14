package com.sksamuel.kotest.engine.extensions.guarantees

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class BeforeTestExecutionOrderTest : FunSpec() {

   var order = ""

   override suspend fun beforeTest(testCase: TestCase) {
      order += "!!"
   }

   init {


      beforeTest {
         order += "a"
      }

      this@BeforeTestExecutionOrderTest.extensions(object : BeforeTestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            order += "b"
         }
      })

      extension(object : BeforeTestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            order += "c"
         }
      })

      beforeTest {
         order += "d"
      }

      this@BeforeTestExecutionOrderTest.extensions(
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

      extension(object : BeforeTestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            order += "g"
         }
      })

      beforeTest {
         order += "h"
      }

      this@BeforeTestExecutionOrderTest.extensions(object : BeforeTestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            order += "i"
         }
      })

      extension(object : BeforeTestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            order += "j"
         }
      })

      test("beforeTest extensions registered in a spec should be executed in registration order") {
         order shouldBe "!!abcdefghij"
      }
   }
}
