package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe

/**
 * This test seeks to define the order in which [AfterTest] extensions are invoked.
 */
class AfterTestOrderSpecificationTest : FunSpec() {

   private var order = ""

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      order += "a"
   }

   override fun extensions(): List<Extension> {
      return listOf(
         object : AfterTestListener {
            override suspend fun afterTest(testCase: TestCase, result: TestResult) {
               order += "f"
            }
         },
         object : AfterTestListener {
            override suspend fun afterTest(testCase: TestCase, result: TestResult) {
               order += "g"
            }
         },
      )
   }

   init {

      afterProject {
         order shouldBe "fgacbfgaedcbfgaedcbfgacb"
      }

      // afterTest blocks are executed in reverse order, so nested ones run before outer ones
      afterTest {
         order += "b"
      }

      afterTest {
         order += "c"
      }

      test("foo") { }
      context("bar") {

         afterTest {
            order += "d"
         }

         afterTest {
            order += "e"
         }

         test("baz") { }
         test("bing") { }
      }
   }
}
